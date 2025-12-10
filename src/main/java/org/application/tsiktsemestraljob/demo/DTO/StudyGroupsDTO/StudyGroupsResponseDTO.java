package org.application.tsiktsemestraljob.demo.DTO.StudyGroupsDTO;

import java.time.LocalDateTime;

public record StudyGroupsResponseDTO(Long id, String name, String description, Long createdBy, LocalDateTime createdAt, String groupOwner) {}
