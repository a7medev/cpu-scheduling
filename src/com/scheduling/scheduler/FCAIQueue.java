package com.scheduling.scheduler;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import static java.util.Comparator.comparingInt;

public class FCAIQueue {
    final private Queue<Task> arrivalQueue;
    final private PriorityQueue<Task> factorQueue;

    FCAIQueue(FCAIScheduler scheduler) {
        arrivalQueue = new LinkedList<Task>();
        factorQueue = new PriorityQueue<>(comparingInt(scheduler::factor));
    }

    void add(Task task) {
        arrivalQueue.add(task);
        factorQueue.add(task);
    }

    Task pollArrival() {
       Task task =  arrivalQueue.poll();
       factorQueue.remove(task);
       return task;
    }

    Task pollFactor() {
        Task task =  factorQueue.poll();
        arrivalQueue.remove(task);
        return task;
    }

    Task peekFactor() {
      return factorQueue.peek();
    }

    boolean isEmpty() {
        return arrivalQueue.isEmpty();
    }

}
