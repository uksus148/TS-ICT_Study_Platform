package org.application.tsiktsemestraljob.demo.DTO.InvitationsDTO;

import org.application.tsiktsemestraljob.demo.Entities.Invitations;

public class InvitationsMapper {
    public static InviteCreateResponseDTO toCreateDTO(Invitations invitations) {
        return new InviteCreateResponseDTO(
                invitations.getToken(),
                invitations.getExpiresAt()

        );
    }
    public static InviteValidateDTO toValidateDTO(Invitations invitations) {
        return new InviteValidateDTO(
                invitations.getGroupId().getName(),
                invitations.getStatus()
        );
    }

    public static InviteAcceptDTO toAcceptDTO(Invitations invitations) {
        return new InviteAcceptDTO(
                invitations.getGroupId().getGroupId(),
                invitations.getStatus()
        );
    }
}
