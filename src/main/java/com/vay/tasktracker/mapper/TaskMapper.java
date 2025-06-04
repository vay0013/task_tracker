package com.vay.tasktracker.mapper;

import com.vay.tasktracker.controller.dto.TaskDto;
import com.vay.tasktracker.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {
    Task toEntity(TaskDto taskDto);

    List<TaskDto> toEntity(List<Task> tasks);

    TaskDto toTaskDto(Task task);

    List<TaskDto> toTaskDtoList(List<Task> tasks);
}