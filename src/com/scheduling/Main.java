package com.scheduling;

import com.scheduling.scheduler.*;
import com.scheduling.scheduler.Process;

import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Process> processes = List.of(
            new Process("P1", 4, 3, 0, 4, null),
            new Process("P2", 5, 6, 0, 4, null),
            new Process("P3", 6, 7, 0, 4, null),
            new Process("P4", 8, 10, 0, 4, null),
            new Process("P5", 12, 5, 0, 4, null),
            new Process("P6", 24, 2, 0, 4, null),
            new Process("P6", 100, 1, 0, 4, null),
            new Process("P6", 101, 2, 0, 4, null),
            new Process("P6", 102, 12, 0, 4, null)
        );

        Scheduler scheduler = new FCAIScheduler();

        System.out.println(scheduler.schedule(processes).stream().map(e -> e.process().name() + ":" + e.startTime() + "-" + e.endTime()).collect(Collectors.joining(" ")));
    }
}
