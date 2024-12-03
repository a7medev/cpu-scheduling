package com.scheduling.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class PriorityScheduler implements  Scheduler {

    @Override
    public List<ExecutionFrame> schedule(List<Process> processes) {
        List<Process> processList = new ArrayList<>(processes);
        processList.sort((a, b) -> {
            int diff = a.arrivalTime() - b.arrivalTime();
            if (diff == 0)
                return a.priority() - b.priority();
            return diff;
        });

        List<ExecutionFrame> frames = new ArrayList<>();
        PriorityQueue<Process> queue = new PriorityQueue<>((a, b) -> a.priority() - b.priority());

        queue.add(processList.get(0));

        int totalTime = 0;
        int i = 1;
        while (!queue.isEmpty()) {
            Process currentProcess = queue.poll();
            int startTime = Math.max(totalTime, currentProcess.arrivalTime());
            int endTime = startTime + currentProcess.burstTime();

            totalTime = endTime;

            frames.add(new ExecutionFrame(currentProcess, startTime, endTime));

            while (i < processList.size()) {
                Process process = processList.get(i);

                if (process.arrivalTime() > endTime) {
                    break;
                }

                queue.add(process);
                i++;
            }

            if (queue.isEmpty() && i < processList.size()) {
                queue.add(processList.get(i));
                i++;
            }
        }

        return frames;
    }
}
