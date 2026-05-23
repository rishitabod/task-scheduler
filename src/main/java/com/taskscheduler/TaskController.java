package com.taskscheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private PriorityQueueScheduler scheduler;

    // GET all tasks
    @GetMapping
    public List<Task> getAllTasks() {
        return scheduler.getAllTasks();
    }

    // POST add new task
    @PostMapping
    public Task addTask(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        int priority = Integer.parseInt(body.get("priority"));
        String deadline = body.get("deadline");
        return scheduler.addTask(name, priority, deadline);
    }

    // PUT complete a task
    @PutMapping("/{id}/complete")
    public Map<String, Boolean> completeTask(@PathVariable int id) {
        boolean result = scheduler.completeTask(id);
        return Map.of("success", result);
    }

    // DELETE a task
    @DeleteMapping("/{id}")
    public Map<String, Boolean> deleteTask(@PathVariable int id) {
        boolean result = scheduler.deleteTask(id);
        return Map.of("success", result);
    }

    // GET next priority task
    @GetMapping("/next")
    public Task getNextTask() {
        return scheduler.getNextTask();
    }
}