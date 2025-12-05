package org.application.tsiktsemestraljob.demo.DTO.StudyGroupsDTO;

import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;

public class StudyGroupsMapper {
    public static StudyGroups toEntity(StudyGroupsRequestDTO dto) {
        StudyGroups studyGroup = new StudyGroups();
        studyGroup.setName(dto.name());
        studyGroup.setDescription(dto.description());
        return studyGroup;
    }

    public static StudyGroupsResponseDTO toDto(StudyGroups studyGroups) {
        return new StudyGroupsResponseDTO(
                studyGroups.getGroupId(),
                studyGroups.getName(),
                studyGroups.getDescription(),
                studyGroups.getCreatedBy().getId(),
                studyGroups.getCreatedAt()
        );
    }
}
