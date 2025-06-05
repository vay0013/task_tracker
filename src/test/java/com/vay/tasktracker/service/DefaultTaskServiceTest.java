package com.vay.tasktracker.service;

import com.vay.tasktracker.dto.payload.TaskDto;
import com.vay.tasktracker.exception.TaskNotFoundException;
import com.vay.tasktracker.mapper.TaskMapper;
import com.vay.tasktracker.model.Task;
import com.vay.tasktracker.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultTaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private DefaultTaskService taskService;

    private Task task;
    private TaskDto taskDto;
    private UUID taskId;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        task = new Task();
        task.setId(taskId);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setExpiryDate(Instant.now().plusSeconds(3600));

        taskDto = new TaskDto(
            task.getTitle(),
            task.getDescription(),
            task.getExpiryDate()
        );
    }

    @Test
    void findAll_shouldReturnAllTasks() {
        // given
        List<Task> tasks = List.of(task);
        List<TaskDto> expectedDtos = List.of(taskDto);
        when(taskRepository.findAll()).thenReturn(tasks);
        when(taskMapper.toTaskDtoList(tasks)).thenReturn(expectedDtos);

        // when
        List<TaskDto> result = taskService.findAll();

        // then
        assertThat(result).isEqualTo(expectedDtos);
        verify(taskRepository).findAll();
        verify(taskMapper).toTaskDtoList(tasks);
    }

    @Test
    void findById_whenTaskExists_shouldReturnTask() {
        // given
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toTaskDto(task)).thenReturn(taskDto);

        // when
        TaskDto result = taskService.findById(taskId);

        // then
        assertThat(result).isEqualTo(taskDto);
        verify(taskRepository).findById(taskId);
        verify(taskMapper).toTaskDto(task);
    }

    @Test
    void findById_whenTaskDoesNotExist_shouldThrowException() {
        // given
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> taskService.findById(taskId))
            .isInstanceOf(TaskNotFoundException.class)
            .hasMessageContaining(taskId.toString());
    }

    @Test
    void create_shouldSaveNewTask() {
        // given
        when(taskMapper.toEntity(taskDto)).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // when
        taskService.create(taskDto);

        // then
        verify(taskMapper).toEntity(taskDto);
        verify(taskRepository).save(task);
    }

    @Test
    void update_whenTaskExists_shouldUpdateTask() {
        // given
        TaskDto updatedDto = new TaskDto(
            "Updated Title",
            "Updated Description",
            Instant.now().plusSeconds(7200)
        );
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // when
        taskService.update(taskId, updatedDto);

        // then
        assertThat(task.getTitle()).isEqualTo(updatedDto.title());
        assertThat(task.getDescription()).isEqualTo(updatedDto.description());
        assertThat(task.getExpiryDate()).isEqualTo(updatedDto.expiryDate());
        verify(taskRepository).save(task);
    }

    @Test
    void update_whenTaskDoesNotExist_shouldThrowException() {
        // given
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> taskService.update(taskId, taskDto))
            .isInstanceOf(TaskNotFoundException.class)
            .hasMessageContaining(taskId.toString());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void delete_shouldDeleteTask() {
        // when
        taskService.delete(taskId);

        // then
        verify(taskRepository).deleteById(taskId);
    }
} 