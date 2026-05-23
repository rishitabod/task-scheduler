package com.taskscheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Comparator;

@Service
public class PriorityQueueScheduler {

    @Autowired
    private TaskRepository taskRepository;

    // Add a new task
    public Task addTask(String name, int priority, String deadline, int estimatedMinutes, User user) {
        Task task = new Task(name, priority, deadline, estimatedMinutes, user);
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

    // Smart scheduler — picks best tasks within available time
    public List<Task> getTasksForAvailableTime(User user, int availableMinutes) {
        List<Task> allTasks = getAllTasks(user);

        // Only consider incomplete tasks with estimated time set
        PriorityQueue<Task> pq = new PriorityQueue<>(
            Comparator.comparingInt(Task::getPriority)
                      .thenComparing(Task::getDeadline)
        );

        for (Task task : allTasks) {
            if (!task.isCompleted() && task.getEstimatedMinutes() > 0) {
                pq.offer(task);
            }
        }

        // Greedily pick tasks by priority until time runs out
        List<Task> recommended = new ArrayList<>();
        int timeLeft = availableMinutes;

        while (!pq.isEmpty() && timeLeft > 0) {
            Task task = pq.poll();
            if (task.getEstimatedMinutes() <= timeLeft) {
                recommended.add(task);
                timeLeft -= task.getEstimatedMinutes();
            }
        }

        return recommended;
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
    public boolean updateTask(int id, String name, int priority, String deadline, int estimatedMinutes) {
        return taskRepository.findById(id).map(task -> {
            task.setName(name);
            task.setPriority(priority);
            task.setDeadline(deadline);
            task.setEstimatedMinutes(estimatedMinutes);
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