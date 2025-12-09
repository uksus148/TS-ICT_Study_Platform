package org.application.tsiktsemestraljob.demo.Authorization.AuthenticationProcess;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse; // <--- ВАЖНО: Добавлен Response
import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.DTO.Authentication.LoginDTO;
import org.application.tsiktsemestraljob.demo.DTO.Authentication.RegisterDTO;
import org.application.tsiktsemestraljob.demo.DTO.UserDTO.UserMapper;
import org.application.tsiktsemestraljob.demo.DTO.UserDTO.UserResponseDTO;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.application.tsiktsemestraljob.demo.Repository.UserRepository;
import org.application.tsiktsemestraljob.demo.Service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext; // <--- ИСПРАВЛЕНО (была опечатка)
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
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
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    @Operation(
            summary = "Register endpoint",
            description = "This endpoint implement register auth function"
    )
    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody RegisterDTO dto, HttpServletRequest request, HttpServletResponse response) {

        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPasswordHash(passwordEncoder.encode(dto.password()));
        userRepository.save(user);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .roles("USER")
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(context, request, response);

        return UserMapper.toDTO(user);
    }

    @Operation(
            summary = "Login endpoint",
            description = "This endpoint implement a login logic"
    )
    @PostMapping("/login")
    public UserResponseDTO login(@RequestBody LoginDTO dto, HttpServletRequest request, HttpServletResponse response) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(context, request, response);

        CustomUserDetails cud = (CustomUserDetails) auth.getPrincipal();
        User user = cud.getUser();

        return UserMapper.toDTO(user);
    }

    @Operation(
            summary = "Logout",
            description = "This endpoint implement logout function for user"
    )
    @PostMapping("/logout")
    public String logout(HttpServletRequest req) {
        req.getSession().invalidate();
        return "Logged out";
    }
}