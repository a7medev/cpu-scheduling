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
    final double PERCENTAGE = 0.4;

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

        int taskFactor = factor(task);
        if (runningTask == null) {
            runningTask = task;
            startTime = event.time();
            exitEvent = new ProcessExit(process, startTime + task.burstTime());
            quantumEvent = new QuantumExit(process, startTime + task.quantum());

            int quantumThreshold = (int)ceil(task.quantum() * PERCENTAGE);
            quantumThresholdEvent = new QuantumThreshold(process, startTime + quantumThreshold);
            events.add(exitEvent);
            events.add(quantumEvent);
            events.add(quantumThresholdEvent);
            return;
        }

        int remainingQuantum = runningTask.quantum() - (event.time() - startTime);
        var remainingTime = runningTask.burstTime() - (event.time() - startTime);
        boolean isPastThreshold = (runningTask.quantum() - remainingQuantum) >= (PERCENTAGE * runningTask.quantum());
        int runningTaskFactor = factor(runningTask);
        if (taskFactor > runningTaskFactor || !isPastThreshold) {
            // No preemption
            taskQueue.add(task);
        } else {
            // Preemption
            frames.add(new ExecutionFrame(runningTask.process(), startTime, event.time()));

            // Remove the exit event for the running process since it will be rescheduled again with a new
            // exit event.
            events.remove(exitEvent);
            events.remove(quantumEvent);
            events.remove(quantumThresholdEvent);
            var remainingTask = runningTask.copy(remainingTime, remainingQuantum + runningTask.quantum());

            taskQueue.add(remainingTask);
            runningTask = task;
            startTime = event.time();
            exitEvent = new ProcessExit(process, startTime + task.burstTime());
            quantumEvent = new QuantumExit(process, startTime + task.quantum());
            events.add(exitEvent);
            events.add(quantumEvent);
            events.add(quantumThresholdEvent);
        }
    }

    void onProcessExit(ProcessExit event) {
        frames.add(new ExecutionFrame(event.process(), startTime, event.time()));
        events.remove(quantumEvent);
        events.remove(quantumThresholdEvent);

        runningTask = taskQueue.pollArrival();

        if (runningTask == null) {
            return;
        }

        int quantumThreshold = (int)ceil(runningTask.quantum() * PERCENTAGE);
        exitEvent = new ProcessExit(runningTask.process(), event.time() + runningTask.burstTime());
        quantumEvent = new QuantumExit(runningTask.process(), event.time() + runningTask.quantum());
        quantumThresholdEvent = new QuantumThreshold(runningTask.process(), event.time() + quantumThreshold);
        startTime = event.time();

        events.add(exitEvent);
        events.add(quantumEvent);
        events.add(quantumThresholdEvent);
    }

    void onQuantumExit(QuantumExit event) {
        if (taskQueue.isEmpty()) {
            return;
        }
        frames.add(new ExecutionFrame(runningTask.process(), startTime, event.time()));
        // Remove the exit event for the running process since it will be rescheduled again with a new
        // exit event.
        var remainingTime = runningTask.burstTime() - (event.time() - startTime);
        events.remove(exitEvent);
        events.remove(quantumThresholdEvent);
        var remainingTask = runningTask.copy(remainingTime, runningTask.quantum() + 2);

        Task task = taskQueue.pollArrival();

        taskQueue.add(remainingTask);

        int quantumThreshold = (int)ceil(runningTask.quantum() * PERCENTAGE);
        runningTask = task;
        startTime = event.time();
        exitEvent = new ProcessExit(task.process(), startTime + task.burstTime());
        quantumEvent = new QuantumExit(task.process(), startTime + task.quantum());
        quantumThresholdEvent = new QuantumThreshold(runningTask.process(), event.time() + quantumThreshold);
        events.add(exitEvent);
        events.add(quantumEvent);
        events.add(quantumThresholdEvent);
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
        int remainingQuantum = runningTask.quantum() - (event.time() - startTime);
        var remainingTime = runningTask.burstTime() - (event.time() - startTime);

        frames.add(new ExecutionFrame(runningTask.process(), startTime, event.time()));
        // Remove the exit event for the running process since it will be rescheduled again with a new
        // exit event.
        events.remove(exitEvent);
        events.remove(quantumEvent);
        var remainingTask = runningTask.copy(remainingTime, remainingQuantum + runningTask.quantum());
        int quantumThreshold = (int)ceil(runningTask.quantum() * PERCENTAGE);

        taskQueue.add(remainingTask);
        runningTask = task;
        startTime = event.time();
        exitEvent = new ProcessExit(task.process(), startTime + task.burstTime());
        quantumEvent = new QuantumExit(task.process(), startTime + task.quantum());
        quantumThresholdEvent = new QuantumThreshold(runningTask.process(), event.time() + quantumThreshold);
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
