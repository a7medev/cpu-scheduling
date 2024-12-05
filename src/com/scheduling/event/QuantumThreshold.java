package com.scheduling.event;

import com.scheduling.structure.Process;

final public class QuantumThreshold extends SchedulerEvent {
    final Process process;
    final int time;

    public QuantumThreshold(Process process, int time) {
        this.process = process;
        this.time = time;
    }

    @Override
    public Process process() {
        return process;
    }

    @Override
    public int time() {
        return time;
    }
}
