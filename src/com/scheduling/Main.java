package com.scheduling;

import com.scheduling.scheduler.*;
import com.scheduling.scheduler.Process;

import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Process> processes = List.of(
                new Process("P1", 0, 7, 8),
                new Process("P2", 2, 4, 7),
                new Process("P3", 4, 1, 6),
                new Process("P4", 5, 4, 5)
        );

        Scheduler scheduler = new SRTFScheduler();

        System.out.println(scheduler.schedule(processes).stream().map(e -> e.process().name() + ":" + e.startTime() + "-" + e.endTime()).collect(Collectors.joining(" ")));
    }
}
