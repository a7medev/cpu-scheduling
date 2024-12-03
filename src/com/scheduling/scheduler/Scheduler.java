package com.scheduling.scheduler;

import java.util.List;

public interface Scheduler {
    List<ExecutionFrame> schedule(List<Process> processes);
}
