package com.scheduling.scheduler;

import com.scheduling.event.ProcessArrival;
import com.scheduling.event.ProcessExit;
import com.scheduling.event.QuantumExit;
import com.scheduling.event.QuantumThreshold;
import com.scheduling.structure.ExecutionFrame;
import com.scheduling.structure.FCAIQueue;
import com.scheduling.structure.Process;
import com.scheduling.structure.Task;

import java.util.*;

import static java.lang.Math.ceil;
import static java.lang.Math.max;

public class FCAIScheduler extends Scheduler {
    QuantumExit quantumEvent;
    QuantumThreshold quantumThresholdEvent;
    double lastArrivalTime = 0;
    double maxBurstTime = 0;
    final double QUANTUM_THRESHOLD = 0.4;

    FCAIQueue taskQueue = new FCAIQueue(this);

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
        var task = new Task(process, process.burstTime(), process.quantum());

        if (runningTask == null) {
            switchProcess(task, event.time(), 0);
            return;
        }

        var runningTime = event.time() - startTime;
        var remainingQuantum = runningTask.quantum() - runningTime;
        var isPastThreshold = runningTime >= QUANTUM_THRESHOLD * runningTask.quantum();

        if (!isPastThreshold || factor(task) > factor(runningTask)) {
            // No preemption
            taskQueue.add(task);
        } else {
            // Preemption
            switchProcess(task, event.time(), remainingQuantum + runningTask.quantum());
        }
    }

    @Override
    protected void onProcessExit(ProcessExit event) {
        var task = taskQueue.pollArrival();
        switchProcess(task, event.time(), 0);
    }

    @Override
    protected void onQuantumExit(QuantumExit event) {
        if (taskQueue.isEmpty()) {
            return;
        }

        Task task = taskQueue.pollArrival();
        switchProcess(task, event.time(), runningTask.quantum() + 2);
    }

    @Override
    protected void onQuantumThreshold(QuantumThreshold event) {
        if (taskQueue.isEmpty()) {
            return;
        }

        Task task = taskQueue.peekFactor();

        if (factor(task) >= factor(runningTask)) {
            return;
        }

        taskQueue.pollFactor();

        var runningTime = event.time() - startTime;
        int remainingQuantum = runningTask.quantum() - runningTime;

        switchProcess(task, event.time(), runningTask.quantum() + remainingQuantum);
    }

    @Override
    protected void addTask(Task task) {
        taskQueue.add(task);
    }

    @Override
    protected void addRunningTaskEvents(Task task, int time) {
        super.addRunningTaskEvents(task, time);

        var process = task.process();
        var quantumThreshold = (int) ceil(task.quantum() * QUANTUM_THRESHOLD);

        quantumEvent = new QuantumExit(process, startTime + task.quantum());
        quantumThresholdEvent = new QuantumThreshold(process, time + quantumThreshold);

        events.add(quantumEvent);
        events.add(quantumThresholdEvent);
    }

    @Override
    protected void removeRunningTaskEvents() {
        super.removeRunningTaskEvents();
        events.remove(quantumEvent);
        events.remove(quantumThresholdEvent);
    }

    public int factor(Task task) {
        Process process = task.process();
        int normalizedArrivalTime = (int) ceil(process.arrivalTime() / lastArrivalTime);
        int normalizedBurstTime = (int) ceil(task.burstTime() / maxBurstTime);
        return 10 - process.priority() + normalizedArrivalTime + normalizedBurstTime;
    }
}
