package com.vay.tasktracker.service;

import com.vay.tasktracker.controller.dto.TaskDto;
import com.vay.tasktracker.exception.TaskNotFoundException;
import com.vay.tasktracker.mapper.TaskMapper;
import com.vay.tasktracker.model.Task;
import com.vay.tasktracker.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultTaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private DefaultTaskService defaultTaskService;

    @Test
    void findAll_shouldReturnAllTasks() {
        Instant now = Instant.now();
        List<Task> tasks = List.of(
                new Task(UUID.randomUUID(), "t1", "d1", now),
                new Task(UUID.randomUUID(), "t2", "d2", now),
                new Task(UUID.randomUUID(), "t3", "d3", now)
        );
        List<TaskDto> expectedDtos = tasks.stream()
                .map(task -> new TaskDto(task.getTitle(), task.getDescription(), task.getExpiryDate()))
                .toList();
        when(taskRepository.findAll()).thenReturn(tasks);
        when(taskMapper.toTaskDtoList(tasks)).thenReturn(expectedDtos);

        List<TaskDto> result = defaultTaskService.findAll();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(tasks.size());
        assertThat(result)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(expectedDtos);

        verify(taskRepository).findAll();
        verify(taskMapper).toTaskDtoList(tasks);
    }

    @Test
    void findById_whenTaskIsPresent_shouldReturnTask() {
        UUID uuid = UUID.randomUUID();
        Instant now = Instant.now();
        Task task = new Task(uuid, "title", "description", now);
        TaskDto expectedDto = new TaskDto(task.getTitle(), task.getDescription(), task.getExpiryDate());
        when(taskRepository.findById(uuid)).thenReturn(Optional.of(task));
        when(taskMapper.toTaskDto(task)).thenReturn(expectedDto);

        TaskDto result = defaultTaskService.findById(uuid);

        assertThat(result).isNotNull();
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(task);

        verify(taskRepository).findById(uuid);
        verify(taskMapper).toTaskDto(task);
    }

    @Test
    void findById_whenTaskIsNotPresent_shouldThrowException() {
        UUID uuid = UUID.randomUUID();

        var result = assertThrows(TaskNotFoundException.class, () -> defaultTaskService.findById(uuid));

        assertThat(result).isInstanceOf(TaskNotFoundException.class);
        assertThat(result).hasMessage("Task with id %s not found".formatted(uuid));

        verify(taskRepository).findById(uuid);
    }

    @Test
    void create_shouldCreateTask() {
        TaskDto dto = new TaskDto("title", "description", Instant.now());
        Task entity = new Task(UUID.randomUUID(), dto.title(), dto.description(), dto.expiryDate());
        when(taskMapper.toEntity(dto)).thenReturn(entity);

        defaultTaskService.create(dto);

        verify(taskMapper).toEntity(dto);
        verify(taskRepository).save(entity);
    }

    @Test
    void update_whenTaskIsPresent_shouldUpdateTask() {
        UUID uuid = UUID.randomUUID();
        Task existingTask = new Task(uuid, "Old title", "Old description", Instant.now());
        TaskDto updatedTask = new TaskDto("New Title", "New description", Instant.now().plusSeconds(10));
        when(taskRepository.findById(uuid)).thenReturn(Optional.of(existingTask));

        defaultTaskService.update(uuid, updatedTask);

        assertThat(existingTask.getTitle()).isEqualTo(updatedTask.title());
        assertThat(existingTask.getDescription()).isEqualTo(updatedTask.description());
        assertThat(existingTask.getExpiryDate()).isEqualTo(updatedTask.expiryDate());

        verify(taskRepository).findById(uuid);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void update_whenTaskIsNotPresent_shouldThrowException() {
        UUID uuid = UUID.randomUUID();
        TaskDto task = new TaskDto("title", "description", Instant.now());
        when(taskRepository.findById(uuid)).thenReturn(Optional.empty());

        var result = assertThrows(TaskNotFoundException.class, () -> defaultTaskService.update(uuid, task));

        assertThat(result).isInstanceOf(TaskNotFoundException.class);
        assertThat(result).hasMessage("Task with id %s not found".formatted(uuid));

        verify(taskRepository).findById(uuid);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void delete_shouldDeleteTask() {
        UUID uuid = UUID.randomUUID();

        defaultTaskService.delete(uuid);

        verify(taskRepository).deleteById(uuid);
    }
}