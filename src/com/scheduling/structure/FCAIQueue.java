package com.scheduling.structure;

import com.scheduling.scheduler.FCAIScheduler;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import static java.util.Comparator.comparingInt;

public class FCAIQueue {
    final private Queue<Task> arrivalQueue;
    final private PriorityQueue<Task> factorQueue;

    public FCAIQueue(FCAIScheduler scheduler) {
        arrivalQueue = new LinkedList<Task>();
        factorQueue = new PriorityQueue<>(comparingInt(scheduler::factor));
    }

    public void add(Task task) {
        arrivalQueue.add(task);
        factorQueue.add(task);
    }

    public Task pollArrival() {
       Task task =  arrivalQueue.poll();
       factorQueue.remove(task);
       return task;
    }

    public Task pollFactor() {
        Task task =  factorQueue.poll();
        arrivalQueue.remove(task);
        return task;
    }

    public Task peekFactor() {
      return factorQueue.peek();
    }

    public boolean isEmpty() {
        return arrivalQueue.isEmpty();
    }

}
