package org.application.tsiktsemestraljob.demo.DTO.MembershipDTO;

import org.application.tsiktsemestraljob.demo.Entities.Membership;

public class MembershipMapper {

    public static GroupMemberDTO toDTO(Membership m) {
        return new GroupMemberDTO(
                m.getUser().getId(),
                m.getUser().getName(),
                m.getUser().getEmail(),
                m.getMembershipRole()
        );
    }
}