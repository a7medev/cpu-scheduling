package com.scheduling.scheduler;

public abstract sealed class SchedulerEvent implements Comparable<SchedulerEvent> {
    abstract Process process();
    abstract int time();

    @Override
    public int compareTo(SchedulerEvent other) {
        int diff = time() - other.time();
        if (diff == 0) {
            int x = this instanceof ProcessExit ? 1 : 0;
            int y = other instanceof ProcessExit ? 1 : 0;
            return y - x;
        }
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

final class QuantumExit extends SchedulerEvent {
    final Process process;
    final int time;

    public QuantumExit(Process process, int time) {
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
final class QuantumThreshold extends SchedulerEvent {
    final Process process;
    final int time;

    public QuantumThreshold(Process process, int time) {
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
