package com.scheduling.output;

public record Log(String process, int startTime, int endTime, int remainingBurstTime, int initialQuantum,
                  int updatedQuantum, int priority, int initialFactor, int updatedFactor, boolean completed) {
}
