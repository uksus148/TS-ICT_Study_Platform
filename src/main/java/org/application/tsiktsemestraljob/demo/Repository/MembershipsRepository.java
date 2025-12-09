package org.application.tsiktsemestraljob.demo.Repository;

import org.application.tsiktsemestraljob.demo.Entities.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipsRepository extends JpaRepository<Membership, Long> {
    boolean existsByUserIdAndStudyGroupGroupId(Long userId, Long groupId);

    Optional<Membership> findByUserIdAndStudyGroupGroupId(Long userId, Long groupId);

    List<Membership> findAllByUserId(Long userId);

    List<Membership> findAllByStudyGroupGroupId(Long groupId);
}
