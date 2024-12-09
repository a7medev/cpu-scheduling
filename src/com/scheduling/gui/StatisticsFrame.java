package com.scheduling.gui;

import com.scheduling.output.Statistics;
import com.scheduling.output.Logger;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StatisticsFrame extends JFrame {
    final Logger logger;
    final Statistics statistics;

    public StatisticsFrame(Logger logger, Statistics statistics) {
        this.logger = logger;
        this.statistics = statistics;

        setTitle("Scheduling Statistics");

        JPanel mainPanel = new JPanel(new GridLayout(logger == null ? 1 : 2, 1));
        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        mainPanel.setBorder(padding);

        String[] processesColumns = {"Process", "Turnaround Time", "Waiting Time"};
        var processesTableModel = new DefaultTableModel(processesColumns, 0);
        var processesTable = new JTable(processesTableModel);

        var processesScrollPane = new JScrollPane(processesTable);
        processesScrollPane.setBorder(BorderFactory.createTitledBorder("Turnaround/Waiting Times"));

        var turnaroundTimes = statistics.getTurnaroundTimes();
        var waitingTimes = statistics.getWaitingTimes();
        for (var process : turnaroundTimes.keySet()) {
            Object[] row = {process, turnaroundTimes.get(process), waitingTimes.get(process)};
            processesTableModel.addRow(row);
        }

        mainPanel.add(processesScrollPane);

        if (logger != null) {
            String[] logsColumns = {"Time", "Process", "Executed Time", "Remaining Burst Time", "Updated Quantum", "Priority", "FCAI Factor"};
            var logsTableModel = new DefaultTableModel(logsColumns, 0);
            var logsTable = new JTable(logsTableModel);

            var logsScrollPane = new JScrollPane(logsTable);
            logsScrollPane.setBorder(BorderFactory.createTitledBorder("Logs/History"));

            for (var log : logger.getLogs()) {
                var time = log.startTime() + "-" + log.endTime();
                var process = log.process();
                var duration = log.endTime() - log.startTime();
                var burstTime = log.remainingBurstTime();
                var quantum = log.completed() ? "Completed" : log.initialQuantum() + "→" + log.updatedQuantum();
                var priority = log.priority();
                var factor = log.completed() ? "Completed" : log.initialFactor() + "→" + log.updatedFactor();

                Object[] row = {time, process, duration, burstTime, quantum, priority, factor};
                logsTableModel.addRow(row);
            }

            mainPanel.add(logsScrollPane);
        }

        add(mainPanel, BorderLayout.CENTER);
        setSize(new Dimension(800, 600));
    }
}
