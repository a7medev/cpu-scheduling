package com.scheduling.scheduler;

import com.scheduling.output.Log;
import com.scheduling.output.Logger;
import com.scheduling.output.Statistics;
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

    private Logger logger;

    FCAIQueue processQueue = new FCAIQueue(this);

    public FCAIScheduler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public List<ExecutionFrame> schedule(List<com.scheduling.structure.Process> processes, Statistics statistics) {

        for (var process : processes) {
            lastArrivalTime = max(lastArrivalTime, process.arrivalTime());
            maxBurstTime = max(maxBurstTime, process.burstTime());
        }

        lastArrivalTime /= 10.0;
        maxBurstTime /= 10.0;

        return super.schedule(processes, statistics);
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
            var updatedQuantum = remainingQuantum + runningProcess.quantum();
            var previousProcess = runningProcess;
            var previousStartTime = startTime;

            var remainingProcess = switchProcess(process, event.time(), updatedQuantum);

            Log log;
            if (remainingProcess == null) {
                log = new Log(previousProcess.name(), previousStartTime, event.time(), 0, previousProcess.quantum(), updatedQuantum, previousProcess.priority(), factor(previousProcess), 0, true);
            } else {
                log = new Log(previousProcess.name(), previousStartTime, event.time(), remainingProcess.burstTime(), previousProcess.quantum(), updatedQuantum, previousProcess.priority(), factor(previousProcess), factor(remainingProcess), false);
            }
            logger.addLog(log);
        }
    }

    @Override
    protected void onProcessExit(ProcessExit event) {
        super.onProcessExit(event);

        var process = processQueue.pollArrival();

        var log = new Log(runningProcess.name(), startTime, event.time(), 0, runningProcess.quantum(), 0, runningProcess.priority(), factor(runningProcess), 0, true);
        logger.addLog(log);

        switchProcess(process, event.time(), 0);
    }

    @Override
    protected void onQuantumExit(QuantumExit event) {
        if (processQueue.isEmpty()) {
            return;
        }

        Process process = processQueue.pollArrival();

        var previousProcess = runningProcess;

        var previousStartTime = startTime;
        var updatedQuantum = runningProcess.quantum() + 2;
        var remainingProcess = switchProcess(process, event.time(), updatedQuantum);

        assert remainingProcess != null;
        var log = new Log(previousProcess.name(), previousStartTime, event.time(), remainingProcess.burstTime(), previousProcess.quantum(), updatedQuantum, previousProcess.priority(), factor(previousProcess), factor(remainingProcess), false);
        logger.addLog(log);
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

        var previousProcess = runningProcess;

        var runningTime = event.time() - startTime;
        var previousStartTime = startTime;
        int remainingQuantum = runningProcess.quantum() - runningTime;
        var updatedQuantum = runningProcess.quantum() + remainingQuantum;
        var remainingProcess = switchProcess(process, event.time(), updatedQuantum);

        Log log;
        if (remainingProcess == null) {
            log = new Log(previousProcess.name(), previousStartTime, event.time(), 0, previousProcess.quantum(), updatedQuantum, previousProcess.priority(), factor(previousProcess), 0, true);
        } else {
            log = new Log(previousProcess.name(), previousStartTime, event.time(), remainingProcess.burstTime(), previousProcess.quantum(), updatedQuantum, previousProcess.priority(), factor(previousProcess), factor(remainingProcess), false);
        }
        logger.addLog(log);
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
