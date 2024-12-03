package com.scheduling.scheduler;

public record Process(String name,
                      String color,
                      int arrivalTime,
                      int burstTime,
                      int priority) { }
