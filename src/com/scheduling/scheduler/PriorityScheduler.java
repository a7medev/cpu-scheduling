package com.scheduling.scheduler;

import com.scheduling.structure.Process;
import com.scheduling.structure.SchedulerEvent.ProcessArrival;
import com.scheduling.structure.SchedulerEvent.ProcessExit;

import java.util.Comparator;
import java.util.PriorityQueue;

public class PriorityScheduler extends Scheduler {
    PriorityQueue<Process> processQueue = new PriorityQueue<>(Comparator.comparing(Process::priority));

    @Override
    protected void onProcessArrival(ProcessArrival event) {
        var process = event.process();

        if (runningProcess == null) {
            switchProcess(process, event.time());
            return;
        }

        processQueue.add(process);
    }

    @Override
    protected void onProcessExit(ProcessExit event) {
        super.onProcessExit(event);

        var process = processQueue.poll();
        switchProcess(process, event.time());
    }

    @Override
    protected void addProcess(Process process) {
        processQueue.add(process);
    }
}
