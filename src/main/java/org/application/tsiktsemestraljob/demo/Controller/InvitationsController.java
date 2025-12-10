package org.application.tsiktsemestraljob.demo.Controller;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.DTO.InvitationsDTO.InvitationsMapper;
import org.application.tsiktsemestraljob.demo.DTO.InvitationsDTO.InviteAcceptDTO;
import org.application.tsiktsemestraljob.demo.DTO.InvitationsDTO.InviteCreateResponseDTO;
import org.application.tsiktsemestraljob.demo.DTO.InvitationsDTO.InviteValidateDTO;
import org.application.tsiktsemestraljob.demo.Entities.Invitations;
import org.application.tsiktsemestraljob.demo.Service.InvitationsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class InvitationsController {

    private final InvitationsService invitationsService;

    @PostMapping("/{groupId}")
    public InviteCreateResponseDTO createInvitation(@PathVariable Long groupId) {
        Invitations invitation = invitationsService.create(groupId);
        return InvitationsMapper.toCreateDTO(invitation);
    }

    @GetMapping("/validate/{token}")
    public InviteValidateDTO validateInvitation(@PathVariable String token) {
        Invitations invitation = invitationsService.validateToken(token);
        return InvitationsMapper.toValidateDTO(invitation);
    }

    @PostMapping("/accept/{token}")
    public InviteAcceptDTO acceptInvitation(@PathVariable String token) {
        Invitations invitation = invitationsService.acceptInvitation(token);
        return InvitationsMapper.toAcceptDTO(invitation);
    }
}
