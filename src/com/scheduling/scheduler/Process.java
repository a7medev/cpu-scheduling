package com.scheduling.scheduler;

public record Process(String name,
                      int arrivalTime,
                      int burstTime,
                      int priority,
                      int quantum,
                      String color) {}
