package com.scheduling.event;

import com.scheduling.structure.Process;

public abstract sealed class SchedulerEvent implements Comparable<SchedulerEvent> permits ProcessArrival, ProcessExit, QuantumExit, QuantumThreshold {
    abstract public Process process();
    abstract public int time();

    @Override
    public int compareTo(SchedulerEvent other) {
        int diff = time() - other.time();
        if (diff == 0) {
            // If multiple events arrive at the same time, favor process exits to avoid
            // weird behavior like a process being rescheduled with 0 remaining burst time.
            int x = this instanceof ProcessExit ? 1 : 0;
            int y = other instanceof ProcessExit ? 1 : 0;
            return y - x;
        }
        return time() - other.time();
    }
}
