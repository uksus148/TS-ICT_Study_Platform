package org.application.tsiktsemestraljob.demo.Controller;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "Creation of invite",
            description = "This endpoint implement logic that help to create an invitation to group (in token form)"
    )
    @PostMapping("/{groupId}")
    public InviteCreateResponseDTO createInvitation(@PathVariable Long groupId) {
        Invitations invitation = invitationsService.create(groupId);
        return InvitationsMapper.toCreateDTO(invitation);
    }

    @Operation(
            summary = "Validate token",
            description = "This endpoint implement logic that help to validate token and check"
    )
    @GetMapping("/validate/{token}")
    public InviteValidateDTO validateInvitation(@PathVariable String token) {
        Invitations invitation = invitationsService.validateToken(token);
        return InvitationsMapper.toValidateDTO(invitation);
    }

    @Operation(
            summary = "Accept token",
            description = "This endpoint implement logic that help to accept invitation to join in group"
    )
    @PostMapping("/accept/{token}")
    public InviteAcceptDTO acceptInvitation(@PathVariable String token) {
        Invitations invitation = invitationsService.acceptInvitation(token);
        return InvitationsMapper.toAcceptDTO(invitation);
    }
}
