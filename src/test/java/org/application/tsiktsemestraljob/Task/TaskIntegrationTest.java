package org.application.tsiktsemestraljob.Task;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.application.tsiktsemestraljob.IntegrationTest;
import org.application.tsiktsemestraljob.demo.DTO.TaskDTO.TaskRequestDTO;
import org.application.tsiktsemestraljob.demo.DTO.TaskDTO.TaskResponseDTO;
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
@WithMockUser(username = "testmail", roles = {"OWNER"})
public class TaskIntegrationTest extends IntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskPostRequest taskPostRequest;
    @BeforeEach
    void setUp() {
        taskPostRequest = new TaskPostRequest(mockMvc, objectMapper);
    }

    @Test
    void getAllTasks() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk());
    }

    @Test
    void createTask() throws Exception {
        taskPostRequest.postTask("Test Task");
    }

    @Test
    void getTaskById() throws Exception {
        TaskResponseDTO dto = taskPostRequest.postTask("Test Task");
        Long id = dto.id();

        mockMvc.perform(get("/api/tasks/" + id))
                .andExpect(status().isOk());
    }

    @Test
    void updateTask() throws Exception {
        TaskResponseDTO task = taskPostRequest.postTask("Test Task");
        Long id = task.id();

        TaskRequestDTO dto = new TaskRequestDTO(
                "Updated Title",
                task.description(),
                task.deadline(),
                task.status()
        );

        mockMvc.perform(put("/api/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void deleteTask() throws Exception {
        TaskResponseDTO task = taskPostRequest.postTask("Test Task");
        Long id = task.id();

        mockMvc.perform(delete("/api/tasks/" + id))
                .andExpect(status().isOk());
    }
}
