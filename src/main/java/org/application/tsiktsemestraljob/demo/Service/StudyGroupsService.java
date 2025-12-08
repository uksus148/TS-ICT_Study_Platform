package org.application.tsiktsemestraljob.demo.Service;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Authorization.AuthenticationProcess.CurrentUser;
import org.application.tsiktsemestraljob.demo.Entities.Membership;
import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.application.tsiktsemestraljob.demo.Enums.MembershipRole;
import org.application.tsiktsemestraljob.demo.Repository.StudyGroupsRepository;
import org.application.tsiktsemestraljob.demo.Repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyGroupsService {
    private final StudyGroupsRepository studyGroupsRepository;
    private final CurrentUser currentUser;
    private final MembershipService membershipService;
    private final ActivityLogsService activityLogsService;
    private final UserRepository userRepository;

    public StudyGroups create(Long id,StudyGroups group) {
        User creator = currentUser.getCurrentUser();
        User user = userRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if(!creator.getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }

        group.setCreatedBy(creator);

        StudyGroups finalGroup = studyGroupsRepository.save(group);
        membershipService.addMember(creator, finalGroup, MembershipRole.OWNER);

        activityLogsService.log(creator,
                "CREATE_StudyGroup",
                "STUDYGROUP-ID: " + finalGroup.getGroupId());

        return finalGroup;
    }

    @Transactional
    public void joinGroup(Long groupId) {
        User user = currentUser.getCurrentUser();
        StudyGroups group = studyGroupsRepository.findById(groupId).orElseThrow(()
                -> new IllegalArgumentException("Group not found with id " + groupId));

        membershipService.addMember(user, group, MembershipRole.MEMBER);

        activityLogsService.log(user,
                "JOIN_GROUP",
                "STUDYGROUP-ID: " + group.getGroupId()
        );
    }

    public List<StudyGroups> getStudyGroups() {
        return studyGroupsRepository.findAll();
    }

    @Transactional
    public void deleteGroup(Long groupId) {
        User user = currentUser.getCurrentUser();
        if (!membershipService.isOwner(user.getId(), groupId)) {
            throw new AccessDeniedException("Only owner of group can delete group");
        }

        activityLogsService.log(user,
                "DELETE_GROUP",
                "STUDYGROUP-ID: " + groupId);

        studyGroupsRepository.deleteById(groupId);
    }

    public StudyGroups updateStudyGroups(Long id, StudyGroups newStudyGroups) {
        User user = currentUser.getCurrentUser();
        if (!membershipService.isOwner(user.getId(), id)) {
            throw new AccessDeniedException("Only owner of group can update group");
        }

        StudyGroups studyGroups = studyGroupsRepository.findById(id).orElse(null);
        if (studyGroups == null) {throw new IllegalArgumentException("Study groups not found");}
        if(newStudyGroups.getName() != null) {studyGroups.setName(newStudyGroups.getName());}
        if(newStudyGroups.getCreatedBy() != null) {studyGroups.setCreatedBy(newStudyGroups.getCreatedBy());}
        if(newStudyGroups.getDescription() != null) {studyGroups.setDescription(newStudyGroups.getDescription());}

        activityLogsService.log(user,
                "UPDATE_GROUP",
                "STUDYGROUP-ID: " + studyGroups.getGroupId());

        return studyGroupsRepository.save(studyGroups);
    }

    public StudyGroups getStudyGroupById(Long id) {
        return studyGroupsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Study group not found"));
    }
}
