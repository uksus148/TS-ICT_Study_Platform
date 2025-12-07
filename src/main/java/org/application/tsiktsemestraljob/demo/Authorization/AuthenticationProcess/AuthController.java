package org.application.tsiktsemestraljob.demo.Authorization.AuthenticationProcess;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.DTO.Authentication.LoginDTO;
import org.application.tsiktsemestraljob.demo.DTO.Authentication.RegisterDTO;
import org.application.tsiktsemestraljob.demo.Service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserService userService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterDTO dto) {
        userService.register(dto.name(), dto.email(), dto.password());
        return "User registered";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginDTO dto, HttpServletRequest request) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        request.getSession(true);

        return "Logged in";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest req) {
        req.getSession().invalidate();
        return "Logged out";
    }
}