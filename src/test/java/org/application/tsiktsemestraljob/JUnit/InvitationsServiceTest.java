package org.application.tsiktsemestraljob.JUnit;

import org.application.tsiktsemestraljob.demo.Authorization.AuthenticationProcess.CurrentUser;
import org.application.tsiktsemestraljob.demo.Entities.Invitations;
import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.application.tsiktsemestraljob.demo.Enums.AcceptStatus;
import org.application.tsiktsemestraljob.demo.Enums.MembershipRole;
import org.application.tsiktsemestraljob.demo.Repository.InvitationsRepository;
import org.application.tsiktsemestraljob.demo.Repository.StudyGroupsRepository;
import org.application.tsiktsemestraljob.demo.Service.ActivityLogsService;
import org.application.tsiktsemestraljob.demo.Service.InvitationsService;
import org.application.tsiktsemestraljob.demo.Service.MembershipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InvitationsServiceTest {

    @Mock
    private InvitationsRepository invitationsRepository;

    @Mock
    private CurrentUser currentUser;

    @Mock
    private StudyGroupsRepository studyGroupsRepository;

    @Mock
    private MembershipService membershipService;

    @Mock
    private ActivityLogsService activityLogsService;

    @InjectMocks
    private InvitationsService invitationsService;

    private User owner;
    private User member;
    private StudyGroups group;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@mail.com");

        member = new User();
        member.setId(2L);
        member.setEmail("member@mail.com");

        group = new StudyGroups();
        group.setGroupId(10L);
        group.setCreatedBy(owner);
        group.setName("TestGroup");
    }

    private Invitations makeInvitation(AcceptStatus status, LocalDateTime expiresAt) {
        Invitations inv = new Invitations();
        inv.setId(100L);
        inv.setGroupId(group);
        inv.setCreatedBy(owner);
        inv.setToken("abc123");
        inv.setStatus(status);
        inv.setCreatedAt(LocalDateTime.now());
        inv.setExpiresAt(expiresAt);
        return inv;
    }

    @Test
    void nonOwnerCannotCreateInvitation() {

        when(currentUser.getCurrentUser()).thenReturn(member);
        when(studyGroupsRepository.findById(10L)).thenReturn(java.util.Optional.of(group));
        when(membershipService.isOwner(2L, 10L)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () ->
                invitationsService.create(10L)
        );
    }


    @Test
    void ownerCreatesInvitationSuccessfully() {

        when(currentUser.getCurrentUser()).thenReturn(owner);
        when(studyGroupsRepository.findById(10L)).thenReturn(java.util.Optional.of(group));
        when(membershipService.isOwner(1L, 10L)).thenReturn(true);

        when(invitationsRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        Invitations result = invitationsService.create(10L);

        assertNotNull(result);
        assertEquals(AcceptStatus.ACTIVE, result.getStatus());
        assertTrue(result.getExpiresAt().isAfter(LocalDateTime.now()));
        assertNotNull(result.getToken());
    }

    @Test
    void expiredTokenRejected() {

        Invitations expired = makeInvitation(
                AcceptStatus.ACTIVE,
                LocalDateTime.now().minusDays(1)
        );

        when(invitationsRepository.findInvitationsByToken("abc123"))
                .thenReturn(expired);

        assertThrows(AccessDeniedException.class, () ->
                invitationsService.validateToken("abc123")
        );
    }

    @Test
    void userAcceptsInvitationSuccessfully() {

        Invitations invitation = makeInvitation(
                AcceptStatus.ACTIVE,
                LocalDateTime.now().plusDays(3)
        );

        when(invitationsRepository.findInvitationsByToken("abc123"))
                .thenReturn(invitation);

        when(currentUser.getCurrentUser()).thenReturn(member);

        when(invitationsRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        Invitations result = invitationsService.acceptInvitation("abc123");

        assertNotNull(result);
        assertEquals(AcceptStatus.USED, result.getStatus());
        assertEquals(member.getId(), result.getUsedByUserId());

        verify(membershipService)
                .addMember(member, group, MembershipRole.MEMBER);
    }
}
