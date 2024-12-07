package com.scheduling.scheduler;

import com.scheduling.structure.SchedulerEvent;
import com.scheduling.structure.SchedulerEvent.*;
import com.scheduling.structure.ExecutionFrame;
import com.scheduling.structure.Process;
import com.scheduling.structure.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public abstract class Scheduler {
    protected int startTime = 0;
    protected Task runningTask;

    protected final PriorityQueue<SchedulerEvent> events = new PriorityQueue<>();
    protected List<ExecutionFrame> frames = new ArrayList<>();

    ProcessExit exitEvent;

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
                case QuantumExit quantum -> onQuantumExit(quantum);
                case QuantumThreshold threshold -> onQuantumThreshold(threshold);
            }
        }

        return frames;
    }

    abstract protected void onProcessArrival(ProcessArrival event);

    abstract protected void onProcessExit(ProcessExit event);

    protected void onQuantumExit(QuantumExit event) {
    }

    protected void onQuantumThreshold(QuantumThreshold event) {
    }

    abstract protected void addTask(Task task);

    protected void addRunningTaskEvents(Task task, int time) {
        exitEvent = new ProcessExit(task.process(), time + task.burstTime());
        events.add(exitEvent);
    }

    protected void removeRunningTaskEvents() {
        events.remove(exitEvent);
    }

    protected void switchProcess(Task task, int time, int quantum) {
        if (runningTask != null) {
            frames.add(new ExecutionFrame(runningTask.process(), startTime, time));

            removeRunningTaskEvents();

            var remainingTime = runningTask.burstTime() - (time - startTime);

            if (remainingTime > 0) {
                var remainingTask = runningTask.copy(remainingTime, quantum, time);
                addTask(remainingTask);
            }
        }

        if (task == null) {
            runningTask = null;
            return;
        }

        runningTask = task;
        startTime = time;

        addRunningTaskEvents(task, time);
    }

    protected void switchProcess(Task task, int time) {
        switchProcess(task, time, 0);
    }
}
