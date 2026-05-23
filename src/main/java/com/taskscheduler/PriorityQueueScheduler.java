package com.taskscheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Comparator;

@Service
public class PriorityQueueScheduler {

    @Autowired
    private TaskRepository taskRepository;

    // Add a new task
    public Task addTask(String name, int priority, String deadline, User user) {
        Task task = new Task(name, priority, deadline, user);
        return taskRepository.save(task);
    }

    // Get all tasks for a user sorted by priority
    public List<Task> getAllTasks(User user) {
        List<Task> tasks = taskRepository.findByUserOrderByPriorityAscDeadlineAsc(user);
        PriorityQueue<Task> pq = new PriorityQueue<>(
            Comparator.comparingInt(Task::getPriority)
                      .thenComparing(Task::getDeadline)
        );
        pq.addAll(tasks);
        List<Task> sorted = new ArrayList<>();
        while (!pq.isEmpty()) sorted.add(pq.poll());
        return sorted;
    }

    // Mark task as completed
    public boolean completeTask(int id) {
        return taskRepository.findById(id).map(task -> {
            task.setCompleted(true);
            taskRepository.save(task);
            return true;
        }).orElse(false);
    }

    // Delete a task
    public boolean deleteTask(int id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Update a task
    public boolean updateTask(int id, String name, int priority, String deadline) {
        return taskRepository.findById(id).map(task -> {
            task.setName(name);
            task.setPriority(priority);
            task.setDeadline(deadline);
            taskRepository.save(task);
            return true;
        }).orElse(false);
    }

    // Get next highest priority task
    public Task getNextTask(User user) {
        List<Task> tasks = getAllTasks(user);
        return tasks.stream()
                    .filter(t -> !t.isCompleted())
                    .findFirst()
                    .orElse(null);
    }
}