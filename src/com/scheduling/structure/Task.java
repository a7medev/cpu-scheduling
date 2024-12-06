package com.scheduling.structure;

public record Task(Process process, int burstTime, int quantum, int arrivalTime) implements Comparable<Task> {

    public Task(Process process, int burstTime, int quantum) {
        this(process, burstTime, quantum, process.arrivalTime());
    }

    public Task copy(int burstTime, int quantum, int arrivalTime) {
        return new Task(process, burstTime, quantum, arrivalTime);
    }

    @Override
    public int compareTo(Task other) {
        var diff = burstTime - other.burstTime;
        if (diff == 0) {
            return process.arrivalTime() - other.process.arrivalTime();
        }
        return diff;
    }
}
