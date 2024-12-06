package com.scheduling;

import com.scheduling.scheduler.*;
import com.scheduling.structure.Process;

import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Process> processes = List.of(
                new Process("P1", 0, 3, 3, 0, null),
                new Process("P2", 1, 10, 6, 0, null),
                new Process("P3", 2, 5, 7, 0, null),
                new Process("P4", 3, 5, 10, 0, null),
                new Process("P5", 4, 5, 5, 0, null),
                new Process("P6", 5, 5, 2, 0, null),
                new Process("P7", 6, 5, 1, 0, null),
                new Process("P8", 7, 5, 2, 0, null),
                new Process("P9", 8, 5, 12, 0, null),
                new Process("P8", 7, 5, 2, 0, null),
                new Process("P9", 8, 5, 12, 0, null),
                new Process("P8", 7, 5, 2, 0, null),
                new Process("P9", 8, 5, 12, 0, null),
                new Process("P8", 7, 5, 2, 0, null),
                new Process("P9", 20, 5, 12, 0, null),
                new Process("P3", 25, 5, 7, 0, null),
                new Process("P4", 30, 5, 10, 0, null),
                new Process("P5", 35, 5, 5, 0, null),
                new Process("P6", 40, 5, 2, 0, null),
                new Process("P7", 45, 5, 1, 0, null),
                new Process("P8", 50, 5, 2, 0, null),
                new Process("P9", 55, 5, 12, 0, null),
                new Process("P8", 60, 5, 2, 0, null),
                new Process("P9", 66, 5, 12, 0, null)
        );

        Scheduler scheduler = new SRTFScheduler();

        System.out.println(scheduler.schedule(processes).stream().map(e -> e.process().name() + ":" + e.startTime() + "-" + e.endTime()).collect(Collectors.joining(" ")));
    }
}
