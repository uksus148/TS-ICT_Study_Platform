package org.application.tsiktsemestraljob.demo.DTO.UserDTO;

import org.application.tsiktsemestraljob.demo.Entities.User;

public class UserMapper {

    public static User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPasswordHash(dto.password());
        return user;
    }

    public static UserResponseDTO toDTO(User entity) {
        return new UserResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getEmail()
        );
    }
}