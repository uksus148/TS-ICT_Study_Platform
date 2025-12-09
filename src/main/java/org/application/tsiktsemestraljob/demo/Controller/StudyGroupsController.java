package org.application.tsiktsemestraljob.demo.Controller;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.DTO.StudyGroupsDTO.StudyGroupsMapper;
import org.application.tsiktsemestraljob.demo.DTO.StudyGroupsDTO.StudyGroupsRequestDTO;
import org.application.tsiktsemestraljob.demo.DTO.StudyGroupsDTO.StudyGroupsResponseDTO;
import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;
import org.application.tsiktsemestraljob.demo.Service.StudyGroupsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/studyGroups")
@RequiredArgsConstructor
public class StudyGroupsController {
    private final StudyGroupsService studyGroupsService;

    @GetMapping
    public List<StudyGroupsResponseDTO> getAll() {
        return studyGroupsService.getStudyGroups()
                .stream()
                .map(StudyGroupsMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public StudyGroupsResponseDTO getById(@PathVariable Long id) {
        StudyGroups studyGroup = studyGroupsService.getStudyGroupById(id);
        return StudyGroupsMapper.toDto(studyGroup);
    }

    @PostMapping("/{groupId}/join")
    public void joinGroup(@PathVariable Long groupId) {
        studyGroupsService.joinGroup(groupId);
    }

    @PostMapping("/{id}")
    public StudyGroupsResponseDTO create(@PathVariable Long id, @RequestBody StudyGroupsRequestDTO dto) {
        StudyGroups studyGroup = StudyGroupsMapper.toEntity(dto);
        StudyGroups saveGroup = studyGroupsService.create(id, studyGroup);
        return StudyGroupsMapper.toDto(saveGroup);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        studyGroupsService.deleteGroup(id);
    }

    @PutMapping("/{id}")
    public StudyGroupsResponseDTO update(@PathVariable Long id, @RequestBody StudyGroupsRequestDTO dto) {
         StudyGroups studyGroup = studyGroupsService.updateStudyGroups(id, StudyGroupsMapper.toEntity(dto));
         return StudyGroupsMapper.toDto(studyGroup);
    }
}
