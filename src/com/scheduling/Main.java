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

//        P1 17 0 4 4  6+[0/2.9] +[17/1.7]=16
//        P2 6  3 9 3 1+[3/2.9]+[6/1.7]=7
//        P3 10 4 3 5  7+[4/2.9]+[10/1.7]=15
//        P4 4 29 10 2 0+[29/2.9]+[4/1.7]=13

        Statistics statistics = new Statistics();
        Logger logger = new Logger();
        Scheduler scheduler = new FCAIScheduler(logger);

        System.out.println(scheduler.schedule(processes, statistics).stream().map(e -> e.process().name() + ":" + e.startTime() + "-" + e.endTime()).collect(Collectors.joining(" ")));
        System.out.println(logger);
    }
}
