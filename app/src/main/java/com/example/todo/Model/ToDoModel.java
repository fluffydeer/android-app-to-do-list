package com.example.todo.Model;

public class ToDoModel {
    private int id, status;     //each task has 3 atributes, status is a boolean variable for db
    private String task;        //text of the task

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}
