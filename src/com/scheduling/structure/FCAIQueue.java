package com.scheduling.structure;

import com.scheduling.scheduler.FCAIScheduler;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import static java.util.Comparator.comparingInt;

public class FCAIQueue {
    final private Queue<Process> arrivalQueue;
    final private PriorityQueue<Process> factorQueue;

    public FCAIQueue(FCAIScheduler scheduler) {
        arrivalQueue = new LinkedList<>();
        factorQueue = new PriorityQueue<>(comparingInt(scheduler::factor));
    }

    public void add(Process task) {
        arrivalQueue.add(task);
        factorQueue.add(task);
    }

    public Process pollArrival() {
       var task = arrivalQueue.poll();
       factorQueue.remove(task);
       return task;
    }

    public Process pollFactor() {
        var task = factorQueue.poll();
        arrivalQueue.remove(task);
        return task;
    }

    public Process peekFactor() {
      return factorQueue.peek();
    }

    public boolean isEmpty() {
        return arrivalQueue.isEmpty();
    }

}
