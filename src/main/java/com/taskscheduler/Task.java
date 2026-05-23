package com.taskscheduler;

public class Task {
    private static int counter = 1;
    
    private int id;
    private String name;
    private int priority; // 1 = High, 2 = Medium, 3 = Low
    private String deadline;
    private boolean completed;

    public Task(String name, int priority, String deadline) {
        this.id = counter++;
        this.name = name;
        this.priority = priority;
        this.deadline = deadline;
        this.completed = false;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getPriority() { return priority; }
    public String getDeadline() { return deadline; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setName(String name) { this.name = name; }
    public void setPriority(int priority) { this.priority = priority; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
}
