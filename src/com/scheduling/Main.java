package com.scheduling;

import com.scheduling.scheduler.*;
import com.scheduling.structure.Process;

import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Process> processes = List.of(
                new Process("P1", 0, 3, 3, 4, null),
                new Process("P2", 1, 10, 6, 4, null),
                new Process("P3", 2, 5, 7, 4, null),
                new Process("P4", 3, 5, 10, 4, null),
                new Process("P5", 4, 5, 5, 4, null),
                new Process("P6", 5, 5, 2, 4, null),
                new Process("P7", 6, 5, 1, 4, null),
                new Process("P8", 7, 5, 2, 4, null),
                new Process("P9", 8, 5, 12, 4, null),
                new Process("P10", 7, 5, 2, 4, null),
                new Process("P11", 8, 5, 12, 4, null),
                new Process("P12", 7, 5, 2, 4, null),
                new Process("P13", 8, 5, 12, 4, null),
                new Process("P14", 7, 5, 2, 4, null),
                new Process("P15", 20, 5, 12, 4, null),
                new Process("P16", 25, 5, 7, 4, null),
                new Process("P17", 30, 5, 10, 4, null),
                new Process("P18", 35, 5, 5, 4, null),
                new Process("P19", 40, 5, 2, 4, null),
                new Process("P20", 45, 5, 1, 4, null),
                new Process("P21", 50, 5, 2, 4, null),
                new Process("P22", 55, 5, 12, 4, null),
                new Process("P23", 60, 5, 2, 4, null),
                new Process("P24", 66, 5, 12, 4, null)
        );

        Scheduler scheduler = new FCAIScheduler();

        System.out.println(scheduler.schedule(processes).stream().map(e -> e.process().name() + ":" + e.startTime() + "-" + e.endTime()).collect(Collectors.joining(" ")));
    }
}
