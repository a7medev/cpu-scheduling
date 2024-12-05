package com.scheduling.scheduler;

import java.util.*;

import static java.lang.Math.ceil;
import static java.lang.Math.max;

public class FCAIScheduler implements Scheduler {
    int startTime = 0;
    Task runningTask;
    ProcessExit exitEvent;
    QuantumExit quantumEvent;
    QuantumThreshold quantumThresholdEvent;
    double lastArrivalTime = 0;
    double maxBurstTime = 0;
    final double QUANTUM_THRESHOLD = 0.4;

    PriorityQueue<SchedulerEvent> events = new PriorityQueue<>();
    List<ExecutionFrame> frames = new ArrayList<>();

    FCAIQueue taskQueue = new FCAIQueue(this);

    @Override
    public List<ExecutionFrame> schedule(List<Process> processes) {
        for (var process : processes) {
            var event = new ProcessArrival(process, process.arrivalTime());
            events.add(event);
            lastArrivalTime = max(lastArrivalTime, process.arrivalTime());
            maxBurstTime = max(maxBurstTime, process.burstTime());
        }

        while (!events.isEmpty()) {
            var event = events.poll();

            switch (event) {
                case ProcessArrival arrival -> onProcessArrival(arrival);
                case ProcessExit exit -> onProcessExit(exit);
                case QuantumExit quantum -> onQuantumExit(quantum);
                case QuantumThreshold threshold -> onQuantumThreshold(threshold);
            }
        }

        return frames;
    }

    void onProcessArrival(ProcessArrival event) {
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

    void onProcessExit(ProcessExit event) {
        var task = taskQueue.pollArrival();
        switchProcess(task, event.time(), 0);
    }

    void onQuantumExit(QuantumExit event) {
        if (taskQueue.isEmpty()) {
            return;
        }

        Task task = taskQueue.pollArrival();
        switchProcess(task, event.time(), runningTask.quantum() + 2);
    }

    void onQuantumThreshold(QuantumThreshold event) {
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

    void switchProcess(Task task, int time, int quantum) {
        if (runningTask != null) {
            frames.add(new ExecutionFrame(runningTask.process(), startTime, time));

            events.remove(exitEvent);
            events.remove(quantumEvent);
            events.remove(quantumThresholdEvent);

            var remainingTime = runningTask.burstTime() - (time - startTime);

            if (remainingTime > 0) {
                var remainingTask = runningTask.copy(remainingTime, quantum);
                taskQueue.add(remainingTask);
            }
        }

        if (task == null) {
            return;
        }

        runningTask = task;
        startTime = time;

        var process = task.process();
        var quantumThreshold = (int) ceil(task.quantum() * QUANTUM_THRESHOLD);

        exitEvent = new ProcessExit(process, startTime + task.burstTime());
        quantumEvent = new QuantumExit(process, startTime + task.quantum());
        quantumThresholdEvent = new QuantumThreshold(process, time + quantumThreshold);

        events.add(exitEvent);
        events.add(quantumEvent);
        events.add(quantumThresholdEvent);
    }

    int factor(Task task) {
        Process process = task.process();
        int normalizedArrivalTime = (int) ceil(process.arrivalTime() / lastArrivalTime);
        int normalizedBurstTime = (int) ceil(task.burstTime() / maxBurstTime);
        return 10 - process.priority() + normalizedArrivalTime + normalizedBurstTime;
    }
}
