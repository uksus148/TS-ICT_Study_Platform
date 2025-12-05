package org.application.tsiktsemestraljob.demo.Service;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.application.tsiktsemestraljob.demo.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities("USER") // роли можно расширить
                .build();
    }

    public User register(String name, String email, String rawPassword) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(rawPassword));
        return userRepository.save(user);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) { userRepository.deleteById(id); }

    public User updateUser(Long id, User newUser) {
        User existing = userRepository.findById(id).orElse(null);
        if (existing == null) {
            throw new IllegalArgumentException("User not found with id " + id);
        }
        if (newUser.getName() != null) {
            existing.setName(newUser.getName());
        }
        if (newUser.getEmail() != null) {
            existing.setEmail(newUser.getEmail());
        }
        if (newUser.getPasswordHash() != null) {
            existing.setPasswordHash(newUser.getPasswordHash());
        }
        return userRepository.save(existing);
    }
}
