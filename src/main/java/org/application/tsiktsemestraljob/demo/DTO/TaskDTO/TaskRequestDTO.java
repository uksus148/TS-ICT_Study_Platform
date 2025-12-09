package org.application.tsiktsemestraljob.demo.DTO.TaskDTO;

import org.application.tsiktsemestraljob.demo.Enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskRequestDTO(String title, String description, LocalDateTime deadline, TaskStatus status) {}
