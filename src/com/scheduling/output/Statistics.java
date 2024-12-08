package com.scheduling.output;

import java.util.HashMap;

public class Statistics {

    private HashMap<String, Integer> waitingTimes = new HashMap<>();
    private HashMap<String, Integer> turnaroundTimes = new HashMap<>();

    public void addWaitingTime(String processName, int waitingTime) {
        if (waitingTimes.containsKey(processName)) {
            waitingTime += waitingTimes.get(processName);
        }

        waitingTimes.put(processName, waitingTime);
    }

    public HashMap<String, Integer> getWaitingTimes() {
        return waitingTimes;
    }

    public HashMap<String, Integer> getTurnaroundTimes() {
        return turnaroundTimes;
    }

    public void addTurnaroundTime(String processName, int turnaroundTime) {
        if (turnaroundTimes.containsKey(processName)) {
            turnaroundTime += waitingTimes.get(processName);
        }

        turnaroundTimes.put(processName, turnaroundTime);
    }

    public int getAverageWaitingTime() {
        int averageWaitingTime = 0;
        for (var waitingTime : waitingTimes.values()) {
            averageWaitingTime += waitingTime;
        }

        averageWaitingTime /= waitingTimes.size();

        return averageWaitingTime;
    }

    public int getAverageTurnaroundTime() {
        int averageTurnaroundTime = 0;
        for (var turnaroundTime : turnaroundTimes.values()) {
            averageTurnaroundTime += turnaroundTime;
        }

        averageTurnaroundTime /= turnaroundTimes.size();

        return averageTurnaroundTime;
    }
}
