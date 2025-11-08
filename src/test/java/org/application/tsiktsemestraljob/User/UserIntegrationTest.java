package org.application.tsiktsemestraljob.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.application.tsiktsemestraljob.IntegrationTest;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserIntegrationTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private User newUser;
    private UserPostRequest userPostRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("testname");
        user.setEmail("testemaill@mail");
        user.setPasswordHash("123");
        newUser = new User();
        newUser.setName("testnewname");
        newUser.setEmail("testnewemaill@mail");
        newUser.setPasswordHash("123");
        userPostRequest = new UserPostRequest(mockMvc, objectMapper);
    }


    @Test
    void testGetUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserById() throws Exception {
        User userWithId = userPostRequest.postUser("testname", "testemailll@mail");
        Long id = userWithId.getId();

        mockMvc.perform(get("/api/users/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testname"));
    }

    @Test
    void testCreateUser() throws Exception {
        String json = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateUser() throws Exception {
        User userWithId = userPostRequest.postUser("testname", "ttestemail@mail");
        Long id = userWithId.getId();

        userWithId.setName("testnewname");
        userWithId.setEmail("testnewemaill@mail");

        String updatedjson = objectMapper.writeValueAsString(userWithId);

        mockMvc.perform(put("/api/users/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedjson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testnewname"))
                .andExpect(jsonPath("$.email").value("testnewemaill@mail"));
    }

    @Test
    void testDeleteUser() throws Exception {
        User userWithId = userPostRequest.postUser("testname", "ttestemail@mail");
        Long id = userWithId.getId();

        String json = objectMapper.writeValueAsString(user);

        mockMvc.perform(delete("/api/users/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }
}
