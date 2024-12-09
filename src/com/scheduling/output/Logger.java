package com.scheduling.output;

import java.util.ArrayList;
import java.util.List;

public class Logger {
    List<Log> logs = new ArrayList<>();

    public void addLog(Log log) {
        logs.add(log);
    }

    public List<Log> getLogs() {
        return logs;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("Time    Process     Executed Time   Remaining Burst Time    Updated Quantum     Priority      FCAI Factor\n");
        for (var log : logs) {
            var time = log.startTime() + "-" + log.endTime();
            var process = log.process();
            var duration = log.endTime() - log.startTime();
            var burstTime = log.remainingBurstTime();
            var quantum = log.completed() ? "Completed" : log.initialQuantum() + "→" + log.updatedQuantum();
            var priority = log.priority();
            var factor = log.completed() ? "Completed" : log.initialFactor() + "→" + log.updatedFactor();

            var formattedLog = String.format("%-7s %-11s %-15d %-23d %-19s %-13d %s\n", time, process, duration, burstTime, quantum, priority, factor);
            output.append(formattedLog);
        }
        return output.toString();
    }
}