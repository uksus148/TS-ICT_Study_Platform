package org.application.tsiktsemestraljob.demo.Controller;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.application.tsiktsemestraljob.demo.Service.UserService;
import org.application.tsiktsemestraljob.demo.DTO.UserDTO.UserMapper;
import org.application.tsiktsemestraljob.demo.DTO.UserDTO.UserRequestDTO;
import org.application.tsiktsemestraljob.demo.DTO.UserDTO.UserResponseDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserResponseDTO getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return UserMapper.toDTO(user);
    }

    @GetMapping
    public List<UserResponseDTO> getUsers() {
        return userService.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    @PostMapping
    public UserResponseDTO createUser(@RequestBody UserRequestDTO dto) {
       User user = UserMapper.toEntity(dto);
       User savedUser = userService.createUser(user);
       return UserMapper.toDTO(savedUser);
    }

    @PutMapping("/{id}")
    public UserResponseDTO updateUser(@PathVariable Long id, @RequestBody UserRequestDTO dto) {
        User updatedUser = userService.updateUser(id, UserMapper.toEntity(dto));
        return UserMapper.toDTO(updatedUser);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

}
