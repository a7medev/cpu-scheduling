package com.scheduling.scheduler;

public record Task(Process process, int burstTime) implements Comparable<Task> {
    Task copy(int burstTime) {
        return new Task(process, burstTime);
    }

    @Override
    public int compareTo(Task other) {
        return burstTime - other.burstTime;
    }
}
