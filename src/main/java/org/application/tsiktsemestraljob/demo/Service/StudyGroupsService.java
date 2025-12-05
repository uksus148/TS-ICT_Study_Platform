package org.application.tsiktsemestraljob.demo.Service;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.application.tsiktsemestraljob.demo.Enums.MembershipRole;
import org.application.tsiktsemestraljob.demo.Repository.StudyGroupsRepository;
import org.application.tsiktsemestraljob.demo.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyGroupsService {
    private final StudyGroupsRepository studyGroupsRepository;
    private final UserRepository userRepository;
    private final MembershipService membershipService;
    private final ActivityLogsService activityLogsService;

    public StudyGroups create(Long userId ,StudyGroups group) {
        User creator = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id " + userId));
        group.setCreatedBy(creator);

        StudyGroups finalGroup = studyGroupsRepository.save(group);
        membershipService.addMember(creator, finalGroup, MembershipRole.OWNER);

        activityLogsService.log(creator,
                "CREATE_StudyGroup",
                "STUDYGROUP-ID: " + finalGroup.getGroupId());

        return finalGroup;
    }

    @Transactional
    public void joinGroup(Long groupId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id " + userId));
        StudyGroups group = studyGroupsRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("Group not found with id " + groupId));

        membershipService.addMember(user, group, MembershipRole.MEMBER);
    }

    public List<StudyGroups> getStudyGroups() {
        return studyGroupsRepository.findAll();
    }

    @Transactional
    public void deleteGroup(Long groupId, Long currentUserId) throws AccessDeniedException {
        if (!membershipService.isOwner(currentUserId, groupId)) {
            throw new AccessDeniedException("Only owner can delete group");
        }
        studyGroupsRepository.deleteById(groupId);
    }

    public StudyGroups updateStudyGroups(Long id, StudyGroups newStudyGroups) {
        StudyGroups studyGroups = studyGroupsRepository.findById(id).orElse(null);
        if (studyGroups == null) {throw new IllegalArgumentException("Study groups not found");}
        if(newStudyGroups.getName() != null) {studyGroups.setName(newStudyGroups.getName());}
        if(newStudyGroups.getCreatedBy() != null) {studyGroups.setCreatedBy(newStudyGroups.getCreatedBy());}
        if(newStudyGroups.getDescription() != null) {studyGroups.setDescription(newStudyGroups.getDescription());}
        return studyGroupsRepository.save(studyGroups);
    }

    public StudyGroups getStudyGroupById(Long id) {
        return studyGroupsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Study group not found"));
    }
}
