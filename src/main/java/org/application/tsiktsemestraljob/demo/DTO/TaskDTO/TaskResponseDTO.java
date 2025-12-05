package org.application.tsiktsemestraljob.demo.DTO.TaskDTO;

import org.application.tsiktsemestraljob.demo.Enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponseDTO(Long id, String title, String description, Long groupId, Long createdBy, LocalDateTime deadline, TaskStatus status, LocalDate createdAt) {}
