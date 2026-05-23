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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Task() {}

    public Task(String name, int priority, String deadline, User user) {
        this.name = name;
        this.priority = priority;
        this.deadline = deadline;
        this.completed = false;
        this.user = user;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getPriority() { return priority; }
    public String getDeadline() { return deadline; }
    public boolean isCompleted() { return completed; }
    public User getUser() { return user; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setName(String name) { this.name = name; }
    public void setPriority(int priority) { this.priority = priority; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public void setUser(User user) { this.user = user; }
}