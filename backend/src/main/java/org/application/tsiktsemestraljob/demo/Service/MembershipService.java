package org.application.tsiktsemestraljob.demo.Service;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Entities.Membership;
import org.application.tsiktsemestraljob.demo.Repository.MembershipsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipService {
    private final MembershipsRepository membershipsRepository;

    public Membership create(Membership membership) {
        return membershipsRepository.save(membership);
    }

    public void deleteById(Long id) {
        membershipsRepository.deleteById(id);
    }

    public List<Membership> findAll() {
        return membershipsRepository.findAll();
    }

    public Membership findById(Long id) {
        return membershipsRepository.findById(id).orElse(null);
    }

}
