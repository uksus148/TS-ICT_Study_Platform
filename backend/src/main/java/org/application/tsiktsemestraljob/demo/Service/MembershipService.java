package org.application.tsiktsemestraljob.demo.Service;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Entities.Membership;
import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.application.tsiktsemestraljob.demo.Repository.MembershipsRepository;
import org.application.tsiktsemestraljob.demo.Repository.StudyGroupsRepository;
import org.application.tsiktsemestraljob.demo.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipService {
    private final MembershipsRepository membershipsRepository;
    private final StudyGroupsRepository studyGroupsRepository;
    private final UserRepository userRepository;

    public Membership create(Long userId, Long groupId, Membership membership) {
        User creator = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id " + userId));
        StudyGroups studyGroup = studyGroupsRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("StudyGroup not found with id " + groupId));

        membership.setStudyGroup(studyGroup);
        membership.setUser(creator);

        return membershipsRepository.save(membership);
    }

    public void deleteById(Long id) {
        membershipsRepository.deleteById(id);
    }

    public List<Membership> findAll() {
        return membershipsRepository.findAll();
    }

    public Membership findById(Long id) {
        return membershipsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Membership not found"));
    }

    public Membership update(Long id , Membership membership) {
        Membership oldMembership = membershipsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Membership not found"));
        if(membership.getRole() != null) {oldMembership.setRole(membership.getRole());}
        return membershipsRepository.save(oldMembership);
    }
}
