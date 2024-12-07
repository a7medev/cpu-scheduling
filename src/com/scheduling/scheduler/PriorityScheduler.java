package com.scheduling.scheduler;

import com.scheduling.structure.SchedulerEvent.ProcessArrival;
import com.scheduling.structure.SchedulerEvent.ProcessExit;
import com.scheduling.structure.Task;

import java.util.Comparator;
import java.util.PriorityQueue;

public class PriorityScheduler extends Scheduler {
    PriorityQueue<Task> taskQueue = new PriorityQueue<>(Comparator.comparing(a -> a.process().priority()));

    @Override
    protected void onProcessArrival(ProcessArrival event) {
        var process = event.process();
        var task = new Task(process, process.burstTime(), process.quantum());

        if (runningTask == null) {
            switchProcess(task, event.time());
            return;
        }

        taskQueue.add(task);
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
