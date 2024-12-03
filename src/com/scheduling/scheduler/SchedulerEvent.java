package com.scheduling.scheduler;

public abstract sealed class SchedulerEvent implements Comparable<SchedulerEvent> {
    abstract Process process();
    abstract int time();

    @Override
    public int compareTo(SchedulerEvent other) {
        return time() - other.time();
    }
}

final class ProcessArrival extends SchedulerEvent {
    final Process process;
    final int time;

    public ProcessArrival(Process process, int time) {
        this.process = process;
        this.time = time;
    }

    @Override
    Process process() {
        return process;
    }

    @Override
    int time() {
        return time;
    }
}

final class ProcessExit extends SchedulerEvent {
    final Process process;
    final int time;

    public ProcessExit(Process process, int time) {
        this.process = process;
        this.time = time;
    }

    @Override
    Process process() {
        return process;
    }

    @Override
    int time() {
        return time;
    }
}
