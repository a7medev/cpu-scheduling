package com.scheduling.structure;

public record ExecutionFrame(Process process,
                             int startTime,
                             int endTime) { }
