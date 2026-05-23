package com.taskscheduler;

import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private int priority;
    private String deadline;
    private boolean completed;
    private int estimatedMinutes;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Task() {}

    public Task(String name, int priority, String deadline, int estimatedMinutes, User user) {
        this.name = name;
        this.priority = priority;
        this.deadline = deadline;
        this.estimatedMinutes = estimatedMinutes;
        this.completed = false;
        this.user = user;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getPriority() { return priority; }
    public String getDeadline() { return deadline; }
    public boolean isCompleted() { return completed; }
    public int getEstimatedMinutes() { return estimatedMinutes; }
    public User getUser() { return user; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setName(String name) { this.name = name; }
    public void setPriority(int priority) { this.priority = priority; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public void setEstimatedMinutes(int estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }
    public void setUser(User user) { this.user = user; }
}