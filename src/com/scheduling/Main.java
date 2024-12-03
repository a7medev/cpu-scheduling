package com.scheduling;

import com.scheduling.scheduler.Process;
import com.scheduling.scheduler.SJFScheduler;
import com.scheduling.scheduler.Scheduler;

import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Process> processes = List.of(
                new Process("P1", 4, 3, 0),
                new Process("P2", 5, 6, 0),
                new Process("P3", 6, 7, 0),
                new Process("P4", 8, 10, 0),
                new Process("P5", 12, 5, 0),
                new Process("P6", 24, 2, 0),
                new Process("P6", 100, 1, 0),
                new Process("P6", 100, 2, 0),
                new Process("P6", 100, 12, 0)
        );

        Scheduler scheduler = new SJFScheduler();

        System.out.println(scheduler.schedule(processes).stream().map(e -> e.startTime() + "-" + e.endTime() + " ").collect(Collectors.joining(" ")));
    }
}
