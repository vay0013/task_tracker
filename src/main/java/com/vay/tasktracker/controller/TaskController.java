package com.vay.tasktracker.controller;

import com.vay.tasktracker.model.Task;
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
    public List<Task> findAll() {
        return taskService.findAll();
    }

    @GetMapping("{id}")
    public Task findById(@PathVariable UUID id) {
        return taskService.findById(id);
    }

    @PostMapping
    public Task create(@RequestBody Task task) {
        return taskService.create(task);
    }

    @PutMapping
    public void update(@RequestBody Task task) {
        taskService.update(task);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable UUID id) {
        taskService.delete(id);
    }
}
