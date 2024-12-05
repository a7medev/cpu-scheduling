package com.scheduling.scheduler;

import java.util.PriorityQueue;

public class SRTFScheduler extends Scheduler {
    PriorityQueue<Task> taskQueue = new PriorityQueue<>();

    @Override
    protected void onProcessArrival(ProcessArrival event) {
        var process = event.process();
        var task = new Task(process, process.burstTime(), process.quantum());

        if (runningTask == null) {
            switchProcess(task, event.time());
            return;
        }

        var remainingTime = runningTask.burstTime() - (event.time() - startTime);

        if (remainingTime <= task.burstTime()) {
            // No preemption
            taskQueue.add(task);
        } else {
            // Preemption
            switchProcess(task, event.time());
        }
    }

    @Override
    protected void onProcessExit(ProcessExit event) {
        var task = taskQueue.poll();
        switchProcess(task, event.time());
    }

    @Override
    protected void addTask(Task task) {
        taskQueue.add(task);
    }
}
