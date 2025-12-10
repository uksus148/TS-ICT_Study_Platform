package org.application.tsiktsemestraljob.demo.DTO.StudyGroupsDTO;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.application.tsiktsemestraljob.demo.Repository.UserRepository;
@RequiredArgsConstructor
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
                studyGroups.getCreatedAt(),
                studyGroups.getCreatedBy().getName()
        );
    }
}
