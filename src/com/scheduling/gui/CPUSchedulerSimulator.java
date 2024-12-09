package com.scheduling.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.gantt.TaskSeriesCollection;

import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.Date;

public class CPUSchedulerSimulator extends JFrame {

    private DefaultTableModel processTableModel;
    private int processCounter = 0; // Counter for unique process numbers
    private JLabel scheduleNameLabel, awtLabel, atatLabel;
    private JPanel chartPanel;

    public CPUSchedulerSimulator() {
        setTitle("CPU Scheduler GUI");

        // Main panel with GridLayout (2 columns)
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // LEFT: Gantt Chart + Statistics + Scheduler ComboBox and Button
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));

        // Scheduler Section: ComboBox and Schedule Button
        JPanel schedulerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel schedulerLabel = new JLabel("Scheduler: ");
        String[] schedulers = {"FCAI", "Priority", "SJF", "SRTF"};
        JComboBox<String> comboBox = new JComboBox<>(schedulers);
        JButton scheduleButton = new JButton("Schedule");
        schedulerPanel.add(schedulerLabel);
        schedulerPanel.add(comboBox);
        schedulerPanel.add(scheduleButton);

        // Create Scheduling Chart panel (but don't add it yet)
        chartPanel = new JPanel();
        chartPanel.setBorder(BorderFactory.createTitledBorder("Scheduling Graph"));

        // Statistics Panel
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));

        scheduleNameLabel = new JLabel("Schedule Name: ");
        awtLabel = new JLabel("AWT: --");
        atatLabel = new JLabel("ATAT: --");

        statsPanel.add(scheduleNameLabel);
        statsPanel.add(awtLabel);
        statsPanel.add(atatLabel);

        leftPanel.add(schedulerPanel, BorderLayout.NORTH); // Scheduler panel on top
        leftPanel.add(chartPanel, BorderLayout.CENTER);
        leftPanel.add(statsPanel, BorderLayout.SOUTH);

        // RIGHT: Process Information Table + Input Form
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));

        // Process Information Table
        String[] processColumnNames = {"Process Number", "Color", "Name", "PID", "Priority"};
        processTableModel = new DefaultTableModel(processColumnNames, 0);
        JTable processTable = new JTable(processTableModel);

        // Custom renderer for the color column
        processTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel colorPanel = new JPanel();
                colorPanel.setBackground((Color) value); // Set background to the Color object
                return colorPanel;
            }
        });

        JScrollPane processScrollPane = new JScrollPane(processTable);
        processScrollPane.setBorder(BorderFactory.createTitledBorder("Processes Information"));

        // Input Form
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JLabel processNameLabel = new JLabel("Process Name:");
        JTextField processNameField = new JTextField(10);
        JLabel processPriorityLabel = new JLabel("Priority:");
        JTextField processPriorityField = new JTextField(10);
        JButton addProcessButton = new JButton("Add Process");

        inputPanel.add(processNameLabel);
        inputPanel.add(processNameField);
        inputPanel.add(processPriorityLabel);
        inputPanel.add(processPriorityField);
        inputPanel.add(new JLabel()); // Empty placeholder for spacing
        inputPanel.add(addProcessButton);

        rightPanel.add(processScrollPane, BorderLayout.CENTER);
        rightPanel.add(inputPanel, BorderLayout.SOUTH);

        // Add components to the main panel
        mainPanel.add(leftPanel);  // Left: Gantt Chart + Statistics + Scheduler
        mainPanel.add(rightPanel); // Right: Process Table + Input Form

        // Set up the frame
        setContentPane(mainPanel);
        pack(); // Adjusts the frame to fit its components
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        // Add action listener for "Add Process" button
        addProcessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String processName = processNameField.getText().trim();
                String priority = processPriorityField.getText().trim();

                // Validate input fields
                if (processName.isEmpty() || priority.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter both Process Name and Priority!");
                    return;
                }

                // Generate unique process number
                int processNumber = processCounter++;

                // Generate random color
                Color randomColor = getRandomColor();

                // Generate a random PID for demonstration purposes
                int pid = 20220 + processCounter;

                // Add new row to process table
                processTableModel.addRow(new Object[]{processNumber, randomColor, processName, pid, priority});

                // Clear input fields
                processNameField.setText("");
                processPriorityField.setText("");
            }
        });

        // Action listener for Schedule button
        scheduleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedScheduler = (String) comboBox.getSelectedItem();
                scheduleNameLabel.setText("Schedule Name: " + selectedScheduler);

                // Generate random AWT and ATAT values
                int awt = getRandomValue();
                int atat = getRandomValue();

                // Update the statistics panel
                awtLabel.setText("AWT: " + awt);
                atatLabel.setText("ATAT: " + atat);

                // Create and display the chart when Schedule button is clicked
                chartPanel.removeAll();
                chartPanel.add(createChartPanel());
                chartPanel.revalidate(); // Refresh the chartPanel
                chartPanel.repaint(); // Redraw the panel
            }
        });
    }

    // Method to create a chart (e.g., bar chart)
    private JPanel createChartPanel() {
        JFreeChart chart = createChart();
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        return chartPanel;
    }

    // Method to create a sample chart
    private JFreeChart createChart() {
        TaskSeriesCollection dataset = createDataset();

        // Create a bar chart
        return ChartFactory.createGanttChart(
                "", // Title
                "Process", // X-axis label
                "Time", // Y-axis label
                dataset, // Dataset
//                PlotOrientation.VERTICAL,
                true, // Include legend
                true, // Tooltips
                false // URLs
        );

        // Customize the chart
//        CategoryPlot plot = (CategoryPlot) chart.getPlot();
//        GanttRenderer renderer = new GanttRenderer();
//        plot.setRenderer(renderer);
    }

    // Helper method to generate random data for the chart
    private int getRandomValue() {
        Random random = new Random();
        return random.nextInt(100) + 1;  // Random value between 1 and 100
    }

    private Color getRandomColor() {
        Random random = new Random();
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    private TaskSeriesCollection createDataset() {
        TaskSeries series1 = new TaskSeries("Scheduled Tasks");
        series1.add(new Task("Process0", new Date(0), new Date(5)));
        series1.add(new Task("Process1", new Date(5), new Date(10)));
        series1.add(new Task("Process2", new Date(10), new Date(15)));
        TaskSeriesCollection dataset = new TaskSeriesCollection();
        dataset.add(series1); return dataset;
    }

    public static void main(String[] args) {
        new CPUSchedulerSimulator();
    }
}
