package org.application.tsiktsemestraljob.demo.Repository;

import org.application.tsiktsemestraljob.demo.Entities.Invitations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationsRepository extends JpaRepository<Invitations, Long> {
    Invitations findInvitationsByToken(String token);
}
