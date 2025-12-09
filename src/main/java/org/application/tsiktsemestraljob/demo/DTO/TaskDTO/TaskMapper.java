package org.application.tsiktsemestraljob.demo.DTO.TaskDTO;

import org.application.tsiktsemestraljob.demo.Entities.Task;

public class TaskMapper {
    public static Task toEntity(TaskRequestDTO dto) {
        Task task = new Task();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setDeadline(dto.deadline());
        task.setStatus(dto.status());
        return task;
    }
    public static TaskResponseDTO toDto(Task task) {
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStudyGroup().getGroupId(),
                task.getCreatedBy().getId(),
                task.getDeadline(),
                task.getStatus(),
                task.getCreatedAt());
    }
}
