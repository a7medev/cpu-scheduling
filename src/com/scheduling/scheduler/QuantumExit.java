package com.scheduling.scheduler;

final public class QuantumExit extends SchedulerEvent {
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
