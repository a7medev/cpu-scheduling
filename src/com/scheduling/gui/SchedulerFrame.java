package com.scheduling.gui;

import com.scheduling.output.*;
import com.scheduling.structure.ExecutionFrame;
import com.scheduling.structure.Process;
import com.scheduling.scheduler.*;

import org.beryx.awt.color.ColorFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.IntervalCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarPainter;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class SchedulerFrame extends JFrame {
    private final DefaultTableModel processTableModel;
    private final JLabel scheduleNameLabel;
    private final JLabel averageWaitLabel;
    private final JLabel averageTurnaroundLabel;
    private final JPanel chartPanel;
    private final List<Process> processes;

    private Logger logger = null;
    private Statistics statistics = null;

    public SchedulerFrame() {
        setTitle("CPU Scheduler");

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        mainPanel.setBorder(padding);

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));

        JPanel schedulerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel schedulerLabel = new JLabel("Scheduler: ");
        String[] schedulers = {"FCAI", "Priority", "SJF", "SRTF"};
        JComboBox<String> comboBox = new JComboBox<>(schedulers);
        JButton scheduleButton = new JButton("Schedule");
        schedulerPanel.add(schedulerLabel);
        schedulerPanel.add(comboBox);
        schedulerPanel.add(scheduleButton);

        chartPanel = new JPanel();
        chartPanel.setBorder(BorderFactory.createTitledBorder("Scheduling Graph"));

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        scheduleNameLabel = new JLabel("Schedule Name: ");
        averageWaitLabel = new JLabel("AWT: --");
        averageTurnaroundLabel = new JLabel("ATAT: --");
        var openStatisticsButton = new JButton("Open Statistics");

        openStatisticsButton.addActionListener(e -> {
            var statisticsFrame = new StatisticsFrame(logger, statistics);
            statisticsFrame.setVisible(true);
        });

        statsPanel.add(scheduleNameLabel);
        statsPanel.add(averageWaitLabel);
        statsPanel.add(averageTurnaroundLabel);
        statsPanel.add(openStatisticsButton);

        leftPanel.add(schedulerPanel, BorderLayout.NORTH);
        leftPanel.add(chartPanel, BorderLayout.CENTER);
        leftPanel.add(statsPanel, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));

        String[] processColumnNames = {"Name", "Color", "Name", "Arrival Time", "Burst Time", "Priority", "Quantum"};
        processTableModel = new DefaultTableModel(processColumnNames, 0);
        JTable processTable = new JTable(processTableModel);

        processTable.getColumnModel().getColumn(1).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JPanel colorPanel = new JPanel();
            colorPanel.setBackground((Color) value);
            return colorPanel;
        });

        JScrollPane processScrollPane = new JScrollPane(processTable);
        processScrollPane.setBorder(BorderFactory.createTitledBorder("Processes Information"));

        // Input Form
        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        JLabel processNameLabel = new JLabel("Process Name:");
        JTextField processNameField = new JTextField(10);
        JLabel arrivalTimeLabel = new JLabel("Arrival Time:");
        JTextField arrivalTimeField = new JTextField(10);
        JLabel burstTimeLabel = new JLabel("Burst Time:");
        JTextField burstTimeField = new JTextField(10);
        JLabel priorityLabel = new JLabel("Priority:");
        JTextField priorityField = new JTextField(10);
        JLabel quantumLabel = new JLabel("Quantum:");
        JTextField quantumField = new JTextField(10);
        JLabel colorLabel = new JLabel("Color:");
        JTextField colorField = new JTextField(10);
        JButton addProcessButton = new JButton("Add Process");

        inputPanel.add(processNameLabel);
        inputPanel.add(processNameField);
        inputPanel.add(arrivalTimeLabel);
        inputPanel.add(arrivalTimeField);
        inputPanel.add(burstTimeLabel);
        inputPanel.add(burstTimeField);
        inputPanel.add(priorityLabel);
        inputPanel.add(priorityField);
        inputPanel.add(quantumLabel);
        inputPanel.add(quantumField);
        inputPanel.add(colorLabel);
        inputPanel.add(colorField);
        inputPanel.add(new JLabel());
        inputPanel.add(addProcessButton);

        rightPanel.add(processScrollPane, BorderLayout.CENTER);
        rightPanel.add(inputPanel, BorderLayout.SOUTH);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        setContentPane(mainPanel);
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        processes = new ArrayList<>();

        // Add action listener for "Add Process" button
        addProcessButton.addActionListener((ActionEvent e) -> {
            String processName = processNameField.getText().trim();
            String arrivalTime = arrivalTimeField.getText().trim();
            String burstTime = burstTimeField.getText().trim();
            String priority = priorityField.getText().trim();
            String quantum = quantumField.getText().trim();
            Color color = ColorFactory.web(colorField.getText().trim());

            // Validate input fields
            if (processName.isEmpty() || arrivalTime.isEmpty() || burstTime.isEmpty() || priority.isEmpty() || quantum.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill in all fields!");
                return;
            }

            Process process = new Process(processName, Integer.parseInt(arrivalTime), Integer.parseInt(burstTime), Integer.parseInt(priority), Integer.parseInt(quantum), color);
            processes.add(process);

            // Add new row to process table
            processTableModel.addRow(new Object[]{processes.size() + 1, color, processName, arrivalTime, burstTime, priority, quantum});

            // Clear input fields
            processNameField.setText("");
            arrivalTimeField.setText("");
            burstTimeField.setText("");
            priorityField.setText("");
            quantumField.setText("");
            colorField.setText("");
        });

        // Action listener for Schedule button
        scheduleButton.addActionListener(e -> {
            if (processTableModel.getRowCount() == 0) { // Check if the processes table is empty
                JOptionPane.showMessageDialog(null, "No processes to schedule! Please add processes first.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                var selectedScheduler = (String) comboBox.getSelectedItem();

                statistics = new Statistics();
                if ("FCAI".equals(selectedScheduler)) {
                    logger = new Logger();
                } else {
                    logger = null;
                }

                Scheduler scheduler = switch (selectedScheduler) {
                    case "SJF" -> new SJFScheduler();
                    case "SRTF" -> new SRTFScheduler();
                    case "Priority" -> new PriorityScheduler();
                    default -> new FCAIScheduler(logger);
                };
                var executionFrames = scheduler.schedule(processes, statistics);

                //update the statistics panel variables
                int awt = statistics.getAverageWaitingTime();
                int atat = statistics.getAverageTurnaroundTime();

                // Update the statistics panel
                scheduleNameLabel.setText("Schedule Name: " + selectedScheduler);
                averageWaitLabel.setText("AWT: " + awt);
                averageTurnaroundLabel.setText("ATAT: " + atat);

                // Create and display the chart when Schedule button is clicked
                chartPanel.removeAll();
                chartPanel.add(createChartPanel(executionFrames));
                chartPanel.revalidate(); // Refresh the chartPanel
                chartPanel.repaint(); // Redraw the panel
            }
        });
    }

    private JPanel createChartPanel(List<ExecutionFrame> executionFrames) {
        JFreeChart chart = createChart(executionFrames);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        return chartPanel;
    }

    private JFreeChart createChart(List<ExecutionFrame> executionFrames) {
        TaskSeriesCollection dataset = createDataset(executionFrames);
        var chart = ChartFactory.createGanttChart(
                null,
                null,
                null,
                dataset,
                false,
                false,
                false
        );

        var plot = (CategoryPlot) chart.getPlot();

        var range = new DateAxis(null);
        var dateFormatter = new SimpleDateFormat("S");
        var renderer = new ChartRenderer();
        range.setDateFormatOverride(dateFormatter);
        plot.setRangeAxis(range);
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.WHITE);

        return chart;
    }

    private TaskSeriesCollection createDataset(List<ExecutionFrame> executionFrames) {
        Task[] frames = new Task[processes.size()];
        TaskSeries ganttChart = new TaskSeries("");

        var rangeStart = executionFrames.getFirst().startTime();
        var rangeEnd = executionFrames.getLast().endTime();
        for (int i = 0; i < processes.size(); i++) {
            var process = processes.get(i);
            frames[i] = new Task(process.name(), new Date(rangeStart), new Date(rangeEnd));
            for (ExecutionFrame frame : executionFrames) {
                if (process.name().equals(frame.process().name())) {
                    Task task = new Task(process.name(), new Date(frame.startTime()), new Date(frame.endTime()));
                    frames[i].addSubtask(task);
                }
            }
            ganttChart.add(frames[i]);
        }

        TaskSeriesCollection dataset = new TaskSeriesCollection();
        dataset.add(ganttChart);
        return dataset;
    }

    class ChartRenderer extends GanttRenderer {
        final private BarPainter barPainter = new StandardBarPainter();

        @Override
        public Paint getItemPaint(int row, int col) {
            return processes.get(col).color();
        }

        @Override
        public BarPainter getBarPainter() {
            return barPainter;
        }

        @Override
        public boolean getShadowsVisible() {
            return false;
        }
    }
}