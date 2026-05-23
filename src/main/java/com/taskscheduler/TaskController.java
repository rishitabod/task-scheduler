package com.taskscheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class TaskController {

    @Autowired
    private PriorityQueueScheduler scheduler;

    @Autowired
    private UserRepository userRepository;

    private User getLoggedInUser(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return null;
        return userRepository.findById(userId).orElse(null);
    }

    // GET all tasks
    @GetMapping
    public Object getAllTasks(HttpSession session) {
        User user = getLoggedInUser(session);
        if (user == null) return unauthorized();
        return scheduler.getAllTasks(user);
    }

    // POST add new task
    @PostMapping
    public Object addTask(@RequestBody Map<String, String> body, HttpSession session) {
        User user = getLoggedInUser(session);
        if (user == null) return unauthorized();
        String name = body.get("name");
        int priority = Integer.parseInt(body.get("priority"));
        String deadline = body.get("deadline");
        int estimatedMinutes = body.get("estimatedMinutes") != null ?
            Integer.parseInt(body.get("estimatedMinutes")) : 0;
        return scheduler.addTask(name, priority, deadline, estimatedMinutes, user);
    }

    // PUT complete a task
    @PutMapping("/{id}/complete")
    public Map<String, Object> completeTask(@PathVariable int id, HttpSession session) {
        User user = getLoggedInUser(session);
        Map<String, Object> response = new HashMap<>();
        if (user == null) { response.put("success", false); return response; }
        response.put("success", scheduler.completeTask(id));
        return response;
    }

    // PUT update a task
    @PutMapping("/{id}")
    public Map<String, Object> updateTask(@PathVariable int id,
                                           @RequestBody Map<String, String> body,
                                           HttpSession session) {
        User user = getLoggedInUser(session);
        Map<String, Object> response = new HashMap<>();
        if (user == null) { response.put("success", false); return response; }
        String name = body.get("name");
        int priority = Integer.parseInt(body.get("priority"));
        String deadline = body.get("deadline");
        int estimatedMinutes = body.get("estimatedMinutes") != null ?
            Integer.parseInt(body.get("estimatedMinutes")) : 0;
        response.put("success", scheduler.updateTask(id, name, priority, deadline, estimatedMinutes));
        return response;
    }

    // DELETE a task
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteTask(@PathVariable int id, HttpSession session) {
        User user = getLoggedInUser(session);
        Map<String, Object> response = new HashMap<>();
        if (user == null) { response.put("success", false); return response; }
        response.put("success", scheduler.deleteTask(id));
        return response;
    }

    // GET next priority task
    @GetMapping("/next")
    public Object getNextTask(HttpSession session) {
        User user = getLoggedInUser(session);
        if (user == null) return unauthorized();
        return scheduler.getNextTask(user);
    }

    // GET smart schedule based on available time
    @GetMapping("/smart-schedule")
    public Object getSmartSchedule(@RequestParam int minutes, HttpSession session) {
        User user = getLoggedInUser(session);
        if (user == null) return unauthorized();
        return scheduler.getTasksForAvailableTime(user, minutes);
    }

    private Map<String, Object> unauthorized() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Please login first!");
        return response;
    }
}