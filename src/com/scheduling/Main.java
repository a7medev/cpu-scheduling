package com.scheduling;

import com.scheduling.scheduler.PriorityScheduler;
import com.scheduling.scheduler.Process;
import com.scheduling.scheduler.SJFScheduler;
import com.scheduling.scheduler.Scheduler;

import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Process> processes = List.of(
                new Process("P1", 4, 3, 8),
                new Process("P2", 5, 6, 7),
                new Process("P3", 6, 7, 6),
                new Process("P4", 8, 10, 5),
                new Process("P5", 12, 5, 4),
                new Process("P6", 24, 2, 3),
                new Process("P7", 100, 1, 2),
                new Process("P8", 100, 2, 1),
                new Process("P9", 100, 12, 0)
        );

        Scheduler scheduler = new PriorityScheduler();

        System.out.println(scheduler.schedule(processes).stream().map(e -> e.startTime() + "-" + e.endTime()).collect(Collectors.joining(" ")));
    }
}
