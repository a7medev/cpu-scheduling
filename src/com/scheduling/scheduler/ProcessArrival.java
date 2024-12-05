package com.scheduling.scheduler;

final public class ProcessArrival extends SchedulerEvent {
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
