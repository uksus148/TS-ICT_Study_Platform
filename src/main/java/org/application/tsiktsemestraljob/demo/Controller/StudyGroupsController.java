package org.application.tsiktsemestraljob.demo.Controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.DTO.MembershipDTO.GroupMemberDTO;
import org.application.tsiktsemestraljob.demo.DTO.MembershipDTO.MembershipMapper;
import org.application.tsiktsemestraljob.demo.DTO.StudyGroupsDTO.StudyGroupsMapper;
import org.application.tsiktsemestraljob.demo.DTO.StudyGroupsDTO.StudyGroupsRequestDTO;
import org.application.tsiktsemestraljob.demo.DTO.StudyGroupsDTO.StudyGroupsResponseDTO;
import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;
import org.application.tsiktsemestraljob.demo.Service.MembershipService;
import org.application.tsiktsemestraljob.demo.Service.StudyGroupsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/studyGroups")
@RequiredArgsConstructor
public class StudyGroupsController {
    private final StudyGroupsService studyGroupsService;
    private final MembershipService membershipService;

    @Operation(
            summary = "Delete Member",
            description = "This endpoint is for delete member from group, uses memberShip Service"
    )
    @DeleteMapping("/{groupId}/members/{memberId}")
    public ResponseEntity<String> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long memberId
    ) {
        membershipService.removeMember(groupId, memberId);
        return ResponseEntity.ok("Member removed");
    }

    @Operation(
            summary = "GetGroupMembers",
            description = "This endpoint implement an get all group members , he take group id like parameter" +
                    "and return an membership service-layer method getGroupMembers"
    )
    @GetMapping("/{groupId}/members")
    public List<GroupMemberDTO> getGroupMembers(@PathVariable Long groupId) {
        return membershipService.getGroupMembers(groupId)
                .stream()
                .map(MembershipMapper::toDTO)
                .toList();
    }

    @Operation(
            summary = "getAll endpoint",
            description = "This endpoint implement an get all study groups logic, he take no parameters " +
                    "and return service-layer method to give all study groups"
    )
    @GetMapping
    public List<StudyGroupsResponseDTO> getAll() {
        return studyGroupsService.getStudyGroups()
                .stream()
                .map(StudyGroupsMapper::toDto)
                .toList();
    }

    @Operation(
            summary = "Get by id endpoint",
            description = "This endpoint implement an get by id logic, he takes an id in parameter and return" +
                    "a service-layer method to get study group by id"
    )
    @GetMapping("/{id}")
    public StudyGroupsResponseDTO getById(@PathVariable Long id) {
        StudyGroups studyGroup = studyGroupsService.getStudyGroupById(id);
        return StudyGroupsMapper.toDto(studyGroup);
    }

    @Operation(
            summary = "Join group method",
            description = "This endpoint implement an join group logic, he takes group id in parameter and return" +
                    "a service-layer method to join group"
    )
    @PostMapping("/{groupId}/join")
    public void joinGroup(@PathVariable Long groupId) {
        studyGroupsService.joinGroup(groupId);
    }

    @Operation(
            summary = "Create endpoint",
            description = "This endpoint implement an join group logic, he takes id and request dto as parameter and return" +
                    "a service-layer method to create a group"
    )
    @PostMapping("/{id}")
    public StudyGroupsResponseDTO create(@PathVariable Long id, @RequestBody StudyGroupsRequestDTO dto) {
        StudyGroups studyGroup = StudyGroupsMapper.toEntity(dto);
        StudyGroups saveGroup = studyGroupsService.create(id, studyGroup);
        return StudyGroupsMapper.toDto(saveGroup);
    }

    @Operation(
            summary = "Delete endpoint",
            description = "This endpoint implement delete logic , he takes id as parameter and return an " +
                    "service-layer method to delete group"
    )
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        studyGroupsService.deleteGroup(id);
    }

    @Operation(
            summary = "Update endpoint",
            description = "This endpoint implement update data logic in study groups, he takes id and request dto as" +
                    "parameter and return a service-layer method to update group"
    )
    @PutMapping("/{id}")
    public StudyGroupsResponseDTO update(@PathVariable Long id, @RequestBody StudyGroupsRequestDTO dto) {
         StudyGroups studyGroup = studyGroupsService.updateStudyGroups(id, StudyGroupsMapper.toEntity(dto));
         return StudyGroupsMapper.toDto(studyGroup);
    }

    @GetMapping("/my-groups")
    public List<StudyGroupsResponseDTO> getMyGroups() {
        List<StudyGroups> groups = membershipService.getUserGroups();
        return groups.stream()
                .map(StudyGroupsMapper::toDto)
                .toList();
    }
}
