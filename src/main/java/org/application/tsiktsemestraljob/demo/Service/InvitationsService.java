package org.application.tsiktsemestraljob.demo.Service;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Authorization.AuthenticationProcess.CurrentUser;
import org.application.tsiktsemestraljob.demo.Entities.Invitations;
import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.application.tsiktsemestraljob.demo.Enums.AcceptStatus;
import org.application.tsiktsemestraljob.demo.Enums.MembershipRole;
import org.application.tsiktsemestraljob.demo.Repository.InvitationsRepository;
import org.application.tsiktsemestraljob.demo.Repository.StudyGroupsRepository;
import org.application.tsiktsemestraljob.demo.Repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitationsService {

    private final InvitationsRepository invitationsRepository;
    private final CurrentUser currentUser;
    private final StudyGroupsRepository studyGroupsRepository;
    private final MembershipService membershipService;
    private final ActivityLogsService activityLogsService;

    @Transactional
    public Invitations create(Long groupId) {
        User user = currentUser.getCurrentUser();

        StudyGroups studyGroup = studyGroupsRepository.findById(groupId)
                .orElseThrow(() -> new IllegalStateException("StudyGroup not found"));

        if(!membershipService.isOwner(user.getId(), groupId)) {
            throw new AccessDeniedException("Only owner of group can create invitations");
        }

        Invitations invitations = new Invitations();
        invitations.setToken(UUID.randomUUID().toString());
        invitations.setCreatedBy(user);
        invitations.setStatus(AcceptStatus.ACTIVE);
        invitations.setGroupId(studyGroup);
        invitations.setExpiresAt(LocalDateTime.now().plusDays(7));
        invitationsRepository.save(invitations);

        activityLogsService.log(user,
                "CREATE_INVINTATION",
        "INVITATION_CREATED_IN_GROUP" + groupId);

        return invitations;
    }

    public Invitations validateToken(String token) {
        Invitations invitation = invitationsRepository.findInvitationsByToken(token);
        AcceptStatus status = invitation.getStatus();

        if(token == null || token.isEmpty()) {
            throw new AccessDeniedException("Token is empty");
        }
        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            invitation.setStatus(AcceptStatus.EXPIRED);
            throw new AccessDeniedException("Token is expired");
        }
        if(status != AcceptStatus.ACTIVE) {
            throw new AccessDeniedException("Token is not active");
        }
        return invitationsRepository.save(invitation);
    }

    public Invitations acceptInvitation(String token) {
        Invitations invitation = invitationsRepository.findInvitationsByToken(token);
        validateToken(token);

        StudyGroups studyGroup = invitation.getGroupId();
        User user = currentUser.getCurrentUser();

        membershipService.addMember(user, studyGroup, MembershipRole.MEMBER);
        invitation.setExpiresAt(LocalDateTime.now());
        invitation.setStatus(AcceptStatus.USED);
        invitation.setUsedByUserId(user.getId());

        activityLogsService.log(user,
                "JOIN_GROUP",
                "TO_GROUP" + studyGroup.getGroupId());

        return invitationsRepository.save(invitation);
    }
}
