package org.application.tsiktsemestraljob.demo.DTO.MembershipDTO;

import org.application.tsiktsemestraljob.demo.Enums.MembershipRole;

public record GroupMemberDTO(
        Long userId,
        String name,
        String email,
        MembershipRole role
) {}
