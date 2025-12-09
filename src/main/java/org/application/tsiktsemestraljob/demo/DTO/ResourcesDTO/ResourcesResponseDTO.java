package org.application.tsiktsemestraljob.demo.DTO.ResourcesDTO;

import java.time.LocalDate;

public record ResourcesResponseDTO(
      Long id,
      Long studyGroup,
      Long uploadedBy,
      String title,
      String type,
      String pathOrUrl,
      LocalDate uploadedAt
){}
