package com.scheduling.scheduler;

import com.scheduling.structure.SchedulerEvent.ProcessArrival;
import com.scheduling.structure.SchedulerEvent.ProcessExit;
import com.scheduling.structure.ExecutionFrame;
import com.scheduling.structure.Process;

import java.util.ArrayList;
import java.util.List;

public class SRTFScheduler extends Scheduler {
    List<Process> processQueue = new ArrayList<>();
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

        if (runningProcess == null) {
            switchProcess(process, event.time());
            return;
        }

        var remainingTime = runningProcess.burstTime() - (event.time() - startTime);

        if (remainingTime <= process.burstTime()) {
            // No preemption
            processQueue.add(process);
        } else {
            // Preemption
            switchProcess(process, event.time());
        }
    }

    @Override
    protected void onProcessExit(ProcessExit event) {
        Process nextProcess = null;
        double burstTime = Integer.MAX_VALUE;

        for (var process : processQueue) {
            var waitingTime = event.time() - process.arrivalTime();
            var effectiveBurstTime = process.burstTime() - agingFactor * waitingTime;

            if (effectiveBurstTime < burstTime) {
                nextProcess = process;
                burstTime = effectiveBurstTime;
            }
        }

        processQueue.remove(nextProcess);
        switchProcess(nextProcess, event.time());
    }

    @Override
    protected void addProcess(Process process) {
        processQueue.add(process);
    }
}
