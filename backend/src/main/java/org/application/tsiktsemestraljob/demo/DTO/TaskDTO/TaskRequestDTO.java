package org.application.tsiktsemestraljob.demo.DTO.TaskDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskRequestDTO(String title, String description, LocalDateTime deadline) {}
