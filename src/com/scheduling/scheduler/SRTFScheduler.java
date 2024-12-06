package com.scheduling.scheduler;

import com.scheduling.event.ProcessArrival;
import com.scheduling.event.ProcessExit;
import com.scheduling.structure.ExecutionFrame;
import com.scheduling.structure.Process;
import com.scheduling.structure.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class SRTFScheduler extends Scheduler {
    List<Task> taskQueue = new ArrayList<>();
    private double agingFactor;

    @Override
    public List<ExecutionFrame> schedule(List<Process> processes) {
        var averageBurstTime = 0;
        for (Process process : processes) {
            averageBurstTime += process.burstTime();
        }
        averageBurstTime /= processes.size();

        agingFactor = 1.0 / averageBurstTime;

        return super.schedule(processes);
    }

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
        Task nextTask = null;
        double burstTime = Integer.MAX_VALUE;

        for (var task : taskQueue) {
            var waitingTime = event.time() - task.arrivalTime();
            var effectiveBurstTime = task.process().burstTime() - agingFactor * waitingTime;

            if (effectiveBurstTime < burstTime) {
                nextTask = task;
                burstTime = effectiveBurstTime;
            }
        }

        taskQueue.remove(nextTask);
        switchProcess(nextTask, event.time());
    }

    @Override
    protected void addTask(Task task) {
        taskQueue.add(task);
    }
}
