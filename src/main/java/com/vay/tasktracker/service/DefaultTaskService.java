package com.vay.tasktracker.service;

import com.vay.tasktracker.controller.dto.TaskDto;
import com.vay.tasktracker.exception.TaskNotFoundException;
import com.vay.tasktracker.mapper.TaskMapper;
import com.vay.tasktracker.model.Task;
import com.vay.tasktracker.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultTaskService implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    public List<TaskDto> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public Task findById(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with id %s not found".formatted(id)));
    }

    @Override
    public Task create(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public void update(Task updatedTask) {
        taskRepository.findById(updatedTask.getId()).ifPresentOrElse(task -> {
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setExpiryDate(updatedTask.getExpiryDate());
            taskRepository.save(task);
        }, () -> {
            throw new TaskNotFoundException("Task with id %s not found".formatted(updatedTask.getId()));
        });
    }

    @Override
    public void delete(UUID id) {
        taskRepository.deleteById(id);
    }
}
