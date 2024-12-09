package com.scheduling.structure;

//import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public sealed interface SchedulerEvent extends Comparable<SchedulerEvent> {
    Process process();
    int time();

    record ProcessArrival(Process process, int time) implements SchedulerEvent { }
    record ProcessExit(Process process, int time) implements SchedulerEvent { }
    record QuantumExit(Process process, int time) implements SchedulerEvent { }
    record QuantumThreshold(Process process, int time) implements SchedulerEvent { }

    /// First compares on the event time, if a tie happens it prioritizes ProcessExit on the rest
    /// based on {@link #eventPriority}.
    @Override
    default int compareTo(/*@NotNull*/ SchedulerEvent other) {
        return Comparator.comparing(SchedulerEvent::time)
                .thenComparing(SchedulerEvent::eventPriority)
                .compare(this, other);
    }

    /// Assigns priorities to events based on their type. This is used when a tie happens in event times.
    /// In general, we want to handle ProcessExit events before the rest of the events, this is critical
    /// since handling a ProcessExit after a ProcessArrival happening at the same instant can lead to
    /// a zero-burst time process waiting in the queue which is not correct.
    private int eventPriority(SchedulerEvent event) {
        return switch (event) {
            case ProcessExit ignored -> 0;
            case QuantumExit ignored -> 1;
            case QuantumThreshold ignored -> 2;
            case ProcessArrival ignored -> 3;
        };
    }
}
