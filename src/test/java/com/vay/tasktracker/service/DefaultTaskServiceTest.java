package com.vay.tasktracker.service;

import com.vay.tasktracker.exception.TaskNotFoundException;
import com.vay.tasktracker.model.Task;
import com.vay.tasktracker.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultTaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private DefaultTaskService defaultTaskService;

    @Test
    void findAll_shouldReturnAllTasks() {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        UUID uuid3 = UUID.randomUUID();
        Instant now = Instant.now();
        List<Task> tasks = List.of(
                new Task(uuid1, "title", "description", now),
                new Task(uuid2, "title", "description", now),
                new Task(uuid3, "title", "description", now));
        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = defaultTaskService.findAll();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(tasks.size());
        assertThat(result)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(tasks);

        verify(taskRepository).findAll();
    }

    @Test
    void findById_whenTaskIsPresent_shouldReturnTask() {
        UUID uuid = UUID.randomUUID();
        Instant now = Instant.now();
        Task task = new Task(uuid, "title", "description", now);
        when(taskRepository.findById(uuid)).thenReturn(Optional.of(task));

        Task result = defaultTaskService.findById(uuid);

        assertThat(result).isNotNull();
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(task);
        verify(taskRepository).findById(uuid);
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
        UUID uuid = UUID.randomUUID();
        Instant now = Instant.now();
        Task task = new Task(uuid, "title", "description", now);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = defaultTaskService.create(task);

        assertThat(result).isNotNull();
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(task);
        assertThat(result).isEqualTo(task);

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void update_whenTaskIsPresent_shouldUpdateTask() {
        UUID uuid = UUID.randomUUID();
        Instant now = Instant.now();
        Task existingTask = new Task(uuid, "Old title", "Old description", now);
        Task updatedTask = new Task(uuid, "New Title", "New description", now);
        when(taskRepository.findById(uuid)).thenReturn(Optional.of(existingTask));

        defaultTaskService.update(updatedTask);

        assertThat(existingTask)
                .usingRecursiveComparison()
                .isEqualTo(updatedTask);

        verify(taskRepository).findById(uuid);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void update_whenTaskIsNotPresent_shouldThrowException() {
        UUID uuid = UUID.randomUUID();
        Task task = new Task(uuid, "title", "description", Instant.now());
        when(taskRepository.findById(uuid)).thenReturn(Optional.empty());

        var result = assertThrows(TaskNotFoundException.class, () -> defaultTaskService.update(task));

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