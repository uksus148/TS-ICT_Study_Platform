package org.application.tsiktsemestraljob.demo.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Authorization.AuthenticationProcess.CurrentUser;
import org.application.tsiktsemestraljob.demo.Entities.Membership;
import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.application.tsiktsemestraljob.demo.Enums.MembershipRole;
import org.application.tsiktsemestraljob.demo.Repository.MembershipsRepository;
import org.application.tsiktsemestraljob.demo.Repository.StudyGroupsRepository;
import org.application.tsiktsemestraljob.demo.Repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipService {
    private final MembershipsRepository membershipRepository;
    private final StudyGroupsRepository studyGroupsRepository;
    private final CurrentUser currentUser;

    public List<Membership> getGroupMembers(Long groupId) {
        User user = currentUser.getCurrentUser();

        StudyGroups group = studyGroupsRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));

        if (!isMember(user.getId(), groupId)) {
            throw new AccessDeniedException("You are not a member of this group");
        }

        return membershipRepository.findAllByStudyGroupGroupId(groupId);
    }

    @Transactional
    public Membership addMember(User user, StudyGroups group, MembershipRole role) {
        if (membershipRepository.existsByUserIdAndStudyGroupGroupId(user.getId(), group.getGroupId())) {
            return membershipRepository.findByUserIdAndStudyGroupGroupId(user.getId(), group.getGroupId())
                    .orElseThrow();
        }

        Membership m = new Membership();
        m.setUser(user);
        m.setStudyGroup(group);
        m.setMembershipRole(role);

        return membershipRepository.save(m);
    }

    public boolean isMember(Long userId, Long groupId) {
        return membershipRepository.existsByUserIdAndStudyGroupGroupId(userId, groupId);
    }

    public boolean isOwner(Long userId, Long groupId) {
        return membershipRepository.findByUserIdAndStudyGroupGroupId(userId, groupId)
                .map(m -> m.getMembershipRole() == MembershipRole.OWNER)
                .orElse(false);
    }
}
