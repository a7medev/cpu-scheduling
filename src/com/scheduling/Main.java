package com.scheduling;

import com.scheduling.output.Statistics;
import com.scheduling.scheduler.*;
import com.scheduling.structure.Process;

import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Process> processes = List.of(
                new Process("P1", 0, 7, 6, 4, null),
                new Process("P2", 2, 4, 7, 4, null),
                new Process("P3", 4, 1, 10, 4, null),
                new Process("P4", 5, 4, 5, 4, null)
        );
        //SJF
//        P0 0
//        P1 6
//        P2 8
//        P3 7
//        P4 3

        //SRTF
//        P1 0 7
//        P2 2 4
//        P3 4 1
//        P4 5 4

        Statistics statistics = new Statistics();
        Scheduler scheduler = new SRTFScheduler();

        System.out.println(scheduler.schedule(processes, statistics).stream().map(e -> e.process().name() + ":" + e.startTime() + "-" + e.endTime()).collect(Collectors.joining(" ")));
        System.out.println(statistics.getTurnaroundTimes());
        System.out.println(statistics.getWaitingTimes());
        System.out.println(statistics.getAverageWaitingTime());
        System.out.println(statistics.getAverageTurnaroundTime());
    }
}
