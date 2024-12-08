package com.scheduling.scheduler;

import com.scheduling.output.Statistics;
import com.scheduling.structure.SchedulerEvent;
import com.scheduling.structure.SchedulerEvent.*;
import com.scheduling.structure.ExecutionFrame;
import com.scheduling.structure.Process;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public abstract class Scheduler {
    protected int startTime = 0;
    protected Process runningProcess;

    protected final PriorityQueue<SchedulerEvent> events = new PriorityQueue<>();
    protected List<ExecutionFrame> frames = new ArrayList<>();
    Statistics statistics = new Statistics();

    ProcessExit exitEvent;

    public List<ExecutionFrame> schedule(List<Process> processes, Statistics statistics) {
        this.statistics = statistics;

        for (var process : processes) {
            var event = new ProcessArrival(process, process.arrivalTime());
            events.add(event);
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

    abstract protected void onProcessArrival(ProcessArrival event);

    protected void onProcessExit(ProcessExit event) {
        var process = event.process();
        var turnaroundTime = event.time() - process.firstArrivalTime();

        statistics.addTurnaroundTime(process.name(), turnaroundTime);
    }

    protected void onQuantumExit(QuantumExit event) {
    }

    protected void onQuantumThreshold(QuantumThreshold event) {
    }

    abstract protected void addProcess(Process process);

    protected void addRunningProcessEvents(Process process, int time) {
        exitEvent = new ProcessExit(process, time + process.burstTime());
        events.add(exitEvent);
    }

    protected void removeRunningProcessEvents() {
        events.remove(exitEvent);
    }

    protected void switchProcess(Process process, int time, int quantum) {
        if (runningProcess != null) {
            frames.add(new ExecutionFrame(runningProcess, startTime, time));

            removeRunningProcessEvents();

            var remainingTime = runningProcess.burstTime() - (time - startTime);

            if (remainingTime > 0) {
                var remainingProcess = runningProcess.copy(remainingTime, quantum, time);
                addProcess(remainingProcess);
            }
        }

        if (process == null) {
            runningProcess = null;
            return;
        }

        runningProcess = process;
        startTime = time;

        var waitingTime = time - process.arrivalTime();

        statistics.addWaitingTime(process.name(), waitingTime);

        addRunningProcessEvents(process, time);
    }

    protected void switchProcess(Process process, int time) {
        switchProcess(process, time, 0);
    }
}
