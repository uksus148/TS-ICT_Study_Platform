package org.application.tsiktsemestraljob.Task;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.application.tsiktsemestraljob.IntegrationTest;
import org.application.tsiktsemestraljob.demo.Entities.Task;
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
public class TaskIntegrationTest extends IntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllTasks() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk());
    }

    @Test
    void createTask() throws Exception {
        TaskPostRequest taskPostRequest = new TaskPostRequest(mockMvc, objectMapper);
        taskPostRequest.postTask("Test Task");
    }

    @Test
    void getTaskById() throws Exception {
        TaskPostRequest taskPostRequest = new TaskPostRequest(mockMvc, objectMapper);
        Task task = taskPostRequest.postTask("Test Task");
        Long id = task.getId();

        mockMvc.perform(get("/api/tasks/" + id))
                .andExpect(status().isOk());
    }

    @Test
    void updateTask() throws Exception {
        TaskPostRequest taskPostRequest = new TaskPostRequest(mockMvc, objectMapper);
        Task task = taskPostRequest.postTask("Test Task");
        Long id = task.getId();

        task.setTitle("Updated Title");

        mockMvc.perform(put("/api/tasks/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void deleteTask() throws Exception {
        TaskPostRequest taskPostRequest = new TaskPostRequest(mockMvc, objectMapper);
        Task task = taskPostRequest.postTask("Test Task");
        Long id = task.getId();

        mockMvc.perform(delete("/api/tasks/" + id))
                .andExpect(status().isOk());
    }
}
