package com.vay.tasktracker.service;

import com.vay.tasktracker.dto.payload.TaskDto;
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
        return taskMapper.toTaskDtoList(taskRepository.findAll());
    }

    @Override
    public TaskDto findById(UUID id) {
        return taskMapper.toTaskDto(taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with id %s not found".formatted(id))));
    }

    @Override
    public void create(TaskDto task) {
        Task entity = taskMapper.toEntity(task);
        taskRepository.save(entity);
    }

    @Override
    public void update(UUID id, TaskDto updatedTask) {
        taskRepository.findById(id).ifPresentOrElse(task -> {
            task.setTitle(updatedTask.title());
            task.setDescription(updatedTask.description());
            task.setExpiryDate(updatedTask.expiryDate());
            taskRepository.save(task);
        }, () -> {
            throw new TaskNotFoundException("Task with id %s not found".formatted(id));
        });
    }

    @Override
    public void delete(UUID id) {
        taskRepository.deleteById(id);
    }
}
