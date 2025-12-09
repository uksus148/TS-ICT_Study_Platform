package org.application.tsiktsemestraljob.demo.Controller;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "Get user by id",
            description = "This endpoint implement an get user by id logic, he takes id as parameter and return" +
                    "a service-layer method who get user by id"
    )
    @GetMapping("/{id}")
    public UserResponseDTO getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return UserMapper.toDTO(user);
    }

    @Operation(
            summary = "Get all users",
            description = "This endpoint implement an get all users logic, he takes no parameters and return" +
                    "a service-layer method who get all users"
    )
    @GetMapping
    public List<UserResponseDTO> getUsers() {
        return userService.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }


    @Operation(
            summary = "Update user",
            description = "This endpoint implements an update user logic, he takes id and request dto as parameters" +
                    "and return a service-layer method who update user"
    )
    @PutMapping("/{id}")
    public UserResponseDTO updateUser(@PathVariable Long id ,@RequestBody UserRequestDTO dto) {
        User updatedUser = userService.updateUser(id, UserMapper.toEntity(dto));
        return UserMapper.toDTO(updatedUser);
    }

    @Operation(
            summary = "Delete user",
            description = "This endpoint implement an delete user by id logic, he takes id as parameter and return" +
                    "a service-layer method who delete user by id"
    )
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

}
