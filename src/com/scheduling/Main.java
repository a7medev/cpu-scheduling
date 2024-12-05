package com.scheduling;

import com.scheduling.scheduler.*;
import com.scheduling.scheduler.Process;

import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Process> processes = List.of(
                new Process("P1", 4, 3, 8, 0, null),
                new Process("P2", 5, 6, 7, 0, null),
                new Process("P3", 6, 7, 6, 0, null),
                new Process("P4", 8, 10, 5, 0, null),
                new Process("P5", 12, 5, 4, 0, null),
                new Process("P6", 24, 2, 3, 0, null),
                new Process("P7", 100, 1, 2, 0, null),
                new Process("P8", 100, 2, 1, 0, null),
                new Process("P9", 100, 12, 0, 0, null)
        );

        Scheduler scheduler = new PriorityScheduler();

        System.out.println(scheduler.schedule(processes).stream().map(e -> e.process().name() + ":" + e.startTime() + "-" + e.endTime()).collect(Collectors.joining(" ")));
    }
}
