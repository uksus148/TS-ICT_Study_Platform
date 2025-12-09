package org.application.tsiktsemestraljob.demo.Authorization.AuthenticationProcess;

import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody RegisterDTO dto, HttpServletRequest request) {

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

        SecurityContextHolder.getContext().setAuthentication(auth);

        request.getSession(true);

        return UserMapper.toDTO(user);
    }

    @PostMapping("/login")
    public UserResponseDTO login(@RequestBody LoginDTO dto, HttpServletRequest request) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        request.getSession(true);

        CustomUserDetails cud = (CustomUserDetails) auth.getPrincipal();
        User user = cud.getUser();

        return UserMapper.toDTO(user);
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest req) {
        req.getSession().invalidate();
        return "Logged out";
    }
}