package org.application.tsiktsemestraljob.demo.Controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Entities.Membership;
import org.application.tsiktsemestraljob.demo.Service.MembershipService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memberships")
@RequiredArgsConstructor
public class MembershipController {
    private final MembershipService membershipService;

    @GetMapping
    public List<Membership> getMemberships() {
        return membershipService.findAll();
    }

    @GetMapping("/{id}")
    public Membership getMembership(@PathVariable Long id) {
        return membershipService.findById(id);
    }

    @PostMapping("{userId}/{groupId}")
    public Membership createMembership(@PathVariable Long userId, @PathVariable Long groupId, @RequestBody Membership membership) {
        return membershipService.create(userId, groupId, membership);
    }

    @DeleteMapping("/{id}")
    public void deleteMembership(@PathVariable Long id) {
        membershipService.deleteById(id);
    }

    @PutMapping("/{id}")
    public Membership updateMembership(@PathVariable Long id, @RequestBody Membership membership) {
        return membershipService.update(id, membership);
    }

}
