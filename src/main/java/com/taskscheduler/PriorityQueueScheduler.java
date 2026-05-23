package com.taskscheduler;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Comparator;

@Service
public class PriorityQueueScheduler {

    // Priority Queue that sorts by priority (1=High first), then by deadline
    private PriorityQueue<Task> taskQueue = new PriorityQueue<>(
        Comparator.comparingInt(Task::getPriority)
                  .thenComparing(Task::getDeadline)
    );

    // Add a new task
    public Task addTask(String name, int priority, String deadline) {
        Task task = new Task(name, priority, deadline);
        taskQueue.offer(task);
        return task;
    }

    // Get all tasks sorted by priority
    public List<Task> getAllTasks() {
        List<Task> sortedTasks = new ArrayList<>(taskQueue);
        Collections.sort(sortedTasks,
            Comparator.comparingInt(Task::getPriority)
                      .thenComparing(Task::getDeadline)
        );
        return sortedTasks;
    }

    // Mark task as completed
    public boolean completeTask(int id) {
        for (Task task : taskQueue) {
            if (task.getId() == id) {
                task.setCompleted(true);
                return true;
            }
        }
        return false;
    }

    // Delete a task
    public boolean deleteTask(int id) {
        return taskQueue.removeIf(task -> task.getId() == id);
    }

    // Get next highest priority task
    public Task getNextTask() {
        return taskQueue.peek();
    }
}