package com.vay.tasktracker.mapper;

import com.vay.tasktracker.dto.payload.TaskDto;
import com.vay.tasktracker.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring")
public interface TaskMapper {
    Task toEntity(TaskDto taskDto);

    List<Task> toEntity(List<TaskDto> taskDtos);

    TaskDto toTaskDto(Task task);

    List<TaskDto> toTaskDtoList(List<Task> tasks);
}