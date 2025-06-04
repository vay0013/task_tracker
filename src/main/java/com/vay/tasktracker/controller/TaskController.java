package com.vay.tasktracker.controller;

import com.vay.tasktracker.controller.dto.TaskDto;
import com.vay.tasktracker.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    public List<TaskDto> findAll() {
        return taskService.findAll();
    }

    @GetMapping("{id}")
    public TaskDto findById(@PathVariable UUID id) {
        return taskService.findById(id);
    }

    @PostMapping
    public void create(@RequestBody TaskDto task) {
        taskService.create(task);
    }

    @PutMapping("{id}")
    public void update(@PathVariable UUID id, @RequestBody TaskDto task) {
        taskService.update(id, task);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable UUID id) {
        taskService.delete(id);
    }
}
