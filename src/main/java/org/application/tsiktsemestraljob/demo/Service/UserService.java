package org.application.tsiktsemestraljob.demo.Service;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.application.tsiktsemestraljob.demo.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

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
        existing.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return userRepository.save(existing);
    }
}
