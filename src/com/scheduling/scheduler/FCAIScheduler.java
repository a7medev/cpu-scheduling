package com.scheduling.scheduler;

import com.scheduling.structure.SchedulerEvent.ProcessArrival;
import com.scheduling.structure.SchedulerEvent.ProcessExit;
import com.scheduling.structure.SchedulerEvent.QuantumExit;
import com.scheduling.structure.SchedulerEvent.QuantumThreshold;
import com.scheduling.structure.ExecutionFrame;
import com.scheduling.structure.FCAIQueue;
import com.scheduling.structure.Process;

import java.util.*;

import static java.lang.Math.ceil;
import static java.lang.Math.max;

public class FCAIScheduler extends Scheduler {
    QuantumExit quantumEvent;
    QuantumThreshold quantumThresholdEvent;
    double lastArrivalTime = 0;
    double maxBurstTime = 0;
    final double QUANTUM_THRESHOLD = 0.4;

    FCAIQueue processQueue = new FCAIQueue(this);

    @Override
    public List<ExecutionFrame> schedule(List<com.scheduling.structure.Process> processes) {
        for (var process : processes) {
            lastArrivalTime = max(lastArrivalTime, process.arrivalTime());
            maxBurstTime = max(maxBurstTime, process.burstTime());
        }

        return super.schedule(processes);
    }

    @Override
    protected void onProcessArrival(ProcessArrival event) {
        var process = event.process();

        if (runningProcess == null) {
            switchProcess(process, event.time(), 0);
            return;
        }

        var runningTime = event.time() - startTime;
        var remainingQuantum = runningProcess.quantum() - runningTime;
        var isPastThreshold = runningTime >= QUANTUM_THRESHOLD * runningProcess.quantum();

        if (!isPastThreshold || factor(process) > factor(runningProcess)) {
            // No preemption
            processQueue.add(process);
        } else {
            // Preemption
            switchProcess(process, event.time(), remainingQuantum + runningProcess.quantum());
        }
    }

    @Override
    protected void onProcessExit(ProcessExit event) {
        var process = processQueue.pollArrival();
        switchProcess(process, event.time(), 0);
    }

    @Override
    protected void onQuantumExit(QuantumExit event) {
        if (processQueue.isEmpty()) {
            return;
        }

        Process process = processQueue.pollArrival();
        switchProcess(process, event.time(), runningProcess.quantum() + 2);
    }

    @Override
    protected void onQuantumThreshold(QuantumThreshold event) {
        if (processQueue.isEmpty()) {
            return;
        }

        Process process = processQueue.peekFactor();

        if (factor(process) >= factor(runningProcess)) {
            return;
        }

        processQueue.pollFactor();

        var runningTime = event.time() - startTime;
        int remainingQuantum = runningProcess.quantum() - runningTime;

        switchProcess(process, event.time(), runningProcess.quantum() + remainingQuantum);
    }

    @Override
    protected void addProcess(Process process) {
        processQueue.add(process);
    }

    @Override
    protected void addRunningProcessEvents(Process process, int time) {
        super.addRunningProcessEvents(process, time);

        var quantumThreshold = (int) ceil(process.quantum() * QUANTUM_THRESHOLD);

        quantumEvent = new QuantumExit(process, startTime + process.quantum());
        quantumThresholdEvent = new QuantumThreshold(process, time + quantumThreshold);

        events.add(quantumEvent);
        events.add(quantumThresholdEvent);
    }

    @Override
    protected void removeRunningProcessEvents() {
        super.removeRunningProcessEvents();
        events.remove(quantumEvent);
        events.remove(quantumThresholdEvent);
    }

    public int factor(Process process) {
        int normalizedArrivalTime = (int) ceil(process.firstArrivalTime() / lastArrivalTime);
        int normalizedBurstTime = (int) ceil(process.burstTime() / maxBurstTime);
        return 10 - process.priority() + normalizedArrivalTime + normalizedBurstTime;
    }
}
