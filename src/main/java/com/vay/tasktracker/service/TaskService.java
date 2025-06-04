package com.vay.tasktracker.service;

import com.vay.tasktracker.controller.dto.TaskDto;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    List<TaskDto> findAll();

    TaskDto findById(UUID id);

    void create(TaskDto task);

    void update(UUID id, TaskDto task);

    void delete(UUID id);
}
