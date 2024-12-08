package com.scheduling;

import com.scheduling.output.Logger;
import com.scheduling.output.Statistics;
import com.scheduling.scheduler.*;
import com.scheduling.structure.Process;

import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Process> processes = List.of(
                new Process("P1", 0, 17, 4, 4, null),
                new Process("P2", 3, 6, 9, 3, null),
                new Process("P3", 4, 10, 3, 5, null),
                new Process("P4", 29, 4, 10, 2, null)
        );

        Statistics statistics = new Statistics();
        Logger logger = new Logger();
        Scheduler scheduler = new FCAIScheduler(logger);

        System.out.println(scheduler.schedule(processes, statistics).stream().map(e -> e.process().name() + ":" + e.startTime() + "-" + e.endTime()).collect(Collectors.joining(" ")));
        System.out.println(logger);
    }
}
