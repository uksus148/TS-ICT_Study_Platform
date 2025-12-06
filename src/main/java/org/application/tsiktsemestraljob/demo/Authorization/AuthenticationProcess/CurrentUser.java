package org.application.tsiktsemestraljob.demo.Authorization.AuthenticationProcess;

import org.application.tsiktsemestraljob.demo.Entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return (User) authentication.getPrincipal();
    }

    public Long id() {
        return getCurrentUser().getId();
    }
}
