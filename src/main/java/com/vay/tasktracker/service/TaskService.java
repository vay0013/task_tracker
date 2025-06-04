package com.vay.tasktracker.service;

import com.vay.tasktracker.model.Task;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    List<Task> findAll();
    Task findById(UUID id);
    Task create(Task task);
    void update(Task task);
    void delete(UUID id);
}
