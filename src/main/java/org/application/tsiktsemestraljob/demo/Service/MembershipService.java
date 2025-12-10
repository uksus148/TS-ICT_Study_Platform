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
    private final CurrentUser currentUser;
    private final ActivityLogsService activityLogsService;
    private final NotificationService notificationService;

    @Transactional
    public List<Membership> getGroupMembers(Long groupId) {
        User user = currentUser.getCurrentUser();

        if (!isMember(user.getId(), groupId)) {
            throw new AccessDeniedException("You are not a member of this group");
        }

        return membershipRepository.findAllByStudyGroupGroupId(groupId);
    }

    public List<StudyGroups> getUserGroups() {
        User user = currentUser.getCurrentUser();

        List<Membership> memberships = membershipRepository.findAllByUserId(user.getId());

        return memberships.stream()
                .map(Membership::getStudyGroup)
                .toList();
    }

    @Transactional
    public void removeMember(Long groupId, Long memberId) {
        User current = currentUser.getCurrentUser();

        if (!isOwner(current.getId(), groupId)) {
            throw new AccessDeniedException("Only owner can remove members");
        }

        Membership membership = membershipRepository
                .findByUserIdAndStudyGroupGroupId(memberId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("User is not a member of this group"));

        if (membership.getMembershipRole() == MembershipRole.OWNER) {
            throw new AccessDeniedException("Cannot remove the owner of the group");
        }

        notificationService.sendToUser(memberId,
                "You have been removed from group " + groupId);
        notificationService.sendToGroup(groupId,
                "User " + memberId + " has been removed");

        membershipRepository.delete(membership);

        activityLogsService.log(
                current,
                "REMOVE_MEMBER",
                "Removed user " + memberId + " from group " + groupId
        );
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
