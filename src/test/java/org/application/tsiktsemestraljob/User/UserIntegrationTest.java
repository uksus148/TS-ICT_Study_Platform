package org.application.tsiktsemestraljob.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.application.tsiktsemestraljob.IntegrationTest;
import org.application.tsiktsemestraljob.demo.DTO.UserDTO.UserMapper;
import org.application.tsiktsemestraljob.demo.DTO.UserDTO.UserRequestDTO;
import org.application.tsiktsemestraljob.demo.DTO.UserDTO.UserResponseDTO;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "testmail")
class UserIntegrationTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRegisterRequest request;
    @BeforeEach
    void setUp() {
        request = new UserRegisterRequest(mockMvc, objectMapper);
    }

    @Test
    void testGetUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserById() throws Exception {
        UserResponseDTO userWithId = request.registeredUser("testname", "testmail", "12345");
        Long id = userWithId.id();

        mockMvc.perform(get("/api/users/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testname"));
    }

    @Test
    void testUpdateUser() throws Exception {
        UserResponseDTO userWithId = request.registeredUser("testname", "testmail", "12345");
        Long id = userWithId.id();

        UserRequestDTO updateUser = new UserRequestDTO(
                "testnewname",
                "testnewemaill@mail",
                "12345"
        );

        mockMvc.perform(put("/api/users/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testnewname"))
                .andExpect(jsonPath("$.email").value("testnewemaill@mail"));
    }

    @Test
    void testDeleteUser() throws Exception {
        UserResponseDTO userWithId = request.registeredUser("testname", "testmail", "12345");
        Long id = userWithId.id();

        mockMvc.perform(delete("/api/users/" + id))
                .andExpect(status().isOk());
    }
}
