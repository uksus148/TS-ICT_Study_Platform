package org.application.tsiktsemestraljob.User;
/*
 * This class created for do a User REGISTER request for easier tests implementation
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.DTO.Authentication.RegisterDTO;
import org.application.tsiktsemestraljob.demo.DTO.UserDTO.UserResponseDTO;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RequiredArgsConstructor
public class UserRegisterRequest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public UserResponseDTO registeredUser(String name, String emailPrefix, String password) throws Exception {
        RegisterDTO dto = new RegisterDTO(name, emailPrefix, password);

        String response = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(response, UserResponseDTO.class);
    }
}
