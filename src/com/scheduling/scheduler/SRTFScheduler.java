package com.scheduling.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class SRTFScheduler implements Scheduler {
    int startTime = 0;
    Task runningTask;
    SchedulerEvent exitEvent;

    PriorityQueue<SchedulerEvent> events = new PriorityQueue<>();
    List<ExecutionFrame> frames = new ArrayList<>();

    PriorityQueue<Task> taskQueue = new PriorityQueue<>();

    @Override
    public List<ExecutionFrame> schedule(List<Process> processes) {
        for (var process : processes) {
            var event = new ProcessArrival(process, process.arrivalTime());
            events.add(event);
        }

        while (!events.isEmpty()) {
            var event = events.poll();

            switch (event) {
                case ProcessArrival arrival -> onProcessArrival(arrival);
                case ProcessExit exit -> onProcessExit(exit);
                case QuantumExit quantum -> {}
                case QuantumThreshold threshold-> {}
            }
        }

        return frames;
    }

    void onProcessArrival(ProcessArrival event) {
        var process = event.process();
        var task = new Task(process, process.burstTime(), process.quantum());

        if (runningTask == null) {
            runningTask = task;
            startTime = event.time();
            exitEvent = new ProcessExit(process, startTime + task.burstTime());
            events.add(exitEvent);
            return;
        }

        var remainingTime = runningTask.burstTime() - (event.time() - startTime);

        if (remainingTime <= task.burstTime()) {
            // No preemption
            taskQueue.add(task);
        } else {
            // Preemption
            frames.add(new ExecutionFrame(runningTask.process(), startTime, event.time()));

            // Remove the exit event for the running process since it will be rescheduled again with a new
            // exit event.
            events.remove(exitEvent);
            var remainingTask = runningTask.copy(remainingTime);

            taskQueue.add(remainingTask);
            runningTask = task;
            startTime = event.time();
            exitEvent = new ProcessExit(process, startTime + task.burstTime());
            events.add(exitEvent);
        }
    }

    void onProcessExit(ProcessExit event) {
        frames.add(new ExecutionFrame(event.process(), startTime, event.time()));

        runningTask = taskQueue.poll();

        if (runningTask == null) {
            return;
        }

        exitEvent = new ProcessExit(runningTask.process(), event.time() + runningTask.burstTime());
        startTime = event.time();

        events.add(exitEvent);
    }
}
