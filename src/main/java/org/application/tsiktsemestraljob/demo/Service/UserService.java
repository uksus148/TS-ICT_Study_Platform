package org.application.tsiktsemestraljob.demo.Service;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Authorization.AuthenticationProcess.CurrentUser;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.application.tsiktsemestraljob.demo.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUser currentUser;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities("USER")
                .build();
    }

    public User register(String name, String email, String rawPassword) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(rawPassword));
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        User toDelete = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        User current = currentUser.getCurrentUser();
        if (!current.getId().equals(id)) {
            throw new SecurityException("You are not allowed to delete other users");
        }
        userRepository.delete(toDelete);
    }

    public User updateUser(Long id, User newUser) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        User current = currentUser.getCurrentUser();
        if(!current.getId().equals(id)) {throw new SecurityException("You are not allowed to update other users"); }

        if (newUser.getName() != null) {
            existing.setName(newUser.getName());
        }
        if (newUser.getEmail() != null) {
            existing.setEmail(newUser.getEmail());
        }
        if (newUser.getPasswordHash() != null && !newUser.getPasswordHash().isBlank()) {
            existing.setPasswordHash(passwordEncoder.encode(newUser.getPasswordHash()));
        }
        return userRepository.save(existing);
    }
}
