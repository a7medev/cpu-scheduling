package com.scheduling.scheduler;

public record ExecutionFrame(Process process,
                             int startTime,
                             int endTime) { }
