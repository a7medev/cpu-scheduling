package com.scheduling.scheduler;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class SJFScheduler implements Scheduler {
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
                default -> throw new IllegalStateException("Unexpected event: " + event);
            }
        }

        return frames;
    }

    void onProcessArrival(ProcessArrival event) {
        var process = event.process();
        var task = new Task(process, process.burstTime(), process.quantum());

        if (runningTask == null) {
            switchProcess(task, event.time());
            return;
        }

        taskQueue.add(task);
    }

    void onProcessExit(ProcessExit event) {
        var task = taskQueue.poll();
        switchProcess(task, event.time());
    }

    void switchProcess(@Nullable Task task, int time) {
        if (runningTask != null) {
            frames.add(new ExecutionFrame(runningTask.process(), startTime, time));

            events.remove(exitEvent);
            var remainingTime = runningTask.burstTime() - (time - startTime);

            if (remainingTime > 0) {
                var remainingTask = runningTask.copy(remainingTime);
                taskQueue.add(remainingTask);
            }
        }

        if (task == null) {
            runningTask = null;
            exitEvent = null;
            return;
        }

        runningTask = task;
        startTime = time;
        exitEvent = new ProcessExit(task.process(), startTime + task.burstTime());
        events.add(exitEvent);
    }
}
