package org.application.tsiktsemestraljob.demo.DTO.InvitationsDTO;

import org.application.tsiktsemestraljob.demo.Enums.AcceptStatus;

public record InviteAcceptDTO(Long groupId, AcceptStatus status) {}
