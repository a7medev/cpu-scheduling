package com.scheduling.structure;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public record Process(String name,
                      int arrivalTime,
                      int firstArrivalTime,
                      int burstTime,
                      int priority,
                      int quantum,
                      String color) implements Comparable<Process> {
    public Process(String name, int arrivalTime, int burstTime, int priority, int quantum, String color) {
        this(name, arrivalTime, arrivalTime, burstTime, priority, quantum, color);
    }

    public Process copy(int burstTime, int quantum, int arrivalTime) {
        return new Process(name, arrivalTime, firstArrivalTime, burstTime, priority, quantum, color);
    }

    @Override
    public int compareTo(@NotNull Process other) {
        return Comparator.comparing(Process::burstTime)
                .thenComparing(Process::arrivalTime)
                .compare(this, other);
    }
}
