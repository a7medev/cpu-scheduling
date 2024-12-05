package com.scheduling.scheduler;

public record Task(Process process, int burstTime, int quantum) implements Comparable<Task> {
    Task copy(int burstTime) {
        return new Task(process, burstTime, quantum);
    }

    Task copy(int burstTime, int quantum) {
        return new Task(process, burstTime, quantum);
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
