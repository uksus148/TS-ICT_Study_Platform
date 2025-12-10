package org.application.tsiktsemestraljob.demo.DTO.InvitationsDTO;

import org.application.tsiktsemestraljob.demo.Enums.AcceptStatus;

public record InviteValidateDTO(String groupName, AcceptStatus valid) {}
