package org.application.tsiktsemestraljob.demo.DTO.ResourcesDTO;

import org.application.tsiktsemestraljob.demo.Entities.Resources;

public class ResourcesMapper {
    public static Resources toEntity(ResourcesRequestDTO dto) {
        Resources resources = new Resources();
        resources.setTitle(dto.title());
        resources.setType(dto.type());
        resources.setPathOrUrl(dto.pathOrUrl());
        return resources;
    }

    public static ResourcesResponseDTO toDto(Resources entity) {
        return new ResourcesResponseDTO(
                entity.getId(),
                entity.getStudyGroup().getGroupId(),
                entity.getUploadedBy().getId(),
                entity.getTitle(),
                entity.getType(),
                entity.getPathOrUrl(),
                entity.getUploadedAt()
        );
    }
}
