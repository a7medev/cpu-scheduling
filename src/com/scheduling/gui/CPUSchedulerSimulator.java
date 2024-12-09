package com.scheduling.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Random;

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
        String[] processColumnNames = {"PNumber", "Color", "Name", "Arrival Time", "Burst Time", "Priority", "Quantum"};
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
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
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
                String arrivalTime = arrivalTimeField.getText().trim();
                String burstTime = burstTimeField.getText().trim();
                String priority = priorityField.getText().trim();
                String quantum = quantumField.getText().trim();

                // Validate input fields
                if (processName.isEmpty() || arrivalTime.isEmpty() || burstTime.isEmpty() || priority.isEmpty() || quantum.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields!");
                    return;
                }

                // Generate random color
                Color randomColor = getRandomColor();

                // Add new row to process table
                processTableModel.addRow(new Object[]{processCounter++, randomColor, processName, arrivalTime, burstTime, priority, quantum});

                // Clear input fields
                processNameField.setText("");
                arrivalTimeField.setText("");
                burstTimeField.setText("");
                priorityField.setText("");
                quantumField.setText("");
            }
        });

        // Action listener for Schedule button
        scheduleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (processTableModel.getRowCount() == 0) { // Check if the processes table is empty
                    JOptionPane.showMessageDialog(null, "No processes to schedule! Please add processes first.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
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
            }
        });
    }

    private JPanel createChartPanel() {
        JFreeChart chart = createChart();
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        return chartPanel;
    }

    private JFreeChart createChart() {
        TaskSeriesCollection dataset = createDataset();
        return ChartFactory.createGanttChart(
                "CPU Scheduling", // Title
                "Processes", // Row key
                "Time", // Column key
                dataset,
                true,
                true,
                false
        );
    }

    private TaskSeriesCollection createDataset() {
        TaskSeries series1 = new TaskSeries("Scheduled Tasks");
        series1.add(new Task("Process0", new Date(0), new Date(5)));
        series1.add(new Task("Process1", new Date(5), new Date(10)));
        series1.add(new Task("Process2", new Date(10), new Date(15)));
        TaskSeriesCollection dataset = new TaskSeriesCollection();
        dataset.add(series1); return dataset;
    }

    private int getRandomValue() {
        Random random = new Random();
        return random.nextInt(50) + 1;
    }

    private Color getRandomColor() {
        Random random = new Random();
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public static void main(String[] args) {
        new CPUSchedulerSimulator();
    }
}
