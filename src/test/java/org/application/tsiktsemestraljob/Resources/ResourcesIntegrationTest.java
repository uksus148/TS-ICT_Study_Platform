package org.application.tsiktsemestraljob.Resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.application.tsiktsemestraljob.IntegrationTest;
import org.application.tsiktsemestraljob.demo.Entities.Resources;
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

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Transactional
public class ResourcesIntegrationTest extends IntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllResources() throws Exception {
        mockMvc.perform(get("/api/resources")).andExpect(status().isOk());
    }

    @Test
    void getResourceById() throws Exception {
        ResourcePostRequest resourcePostRequest = new ResourcePostRequest(mockMvc, objectMapper);
        Resources resource = resourcePostRequest.postResource("Test Resource");
        Long id = resource.getId();

        mockMvc.perform(get("/api/resources/" + id)).andExpect(status().isOk());
    }

    @Test
    void createResource() throws Exception {
        ResourcePostRequest resourcePostRequest = new ResourcePostRequest(mockMvc, objectMapper);
        Resources resource = resourcePostRequest.postResource("Test Resource");
    }

    @Test
    void updateResource() throws Exception {
        ResourcePostRequest resourcePostRequest = new ResourcePostRequest(mockMvc, objectMapper);
        Resources resource = resourcePostRequest.postResource("Test Resource");
        Long id = resource.getId();

        resource.setTitle("Second Resource");
        mockMvc.perform(put("/api/resources/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resource)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Second Resource"));
    }

    @Test
    void deleteResource() throws Exception {
        ResourcePostRequest resourcePostRequest = new ResourcePostRequest(mockMvc, objectMapper);
        Resources resource = resourcePostRequest.postResource("Test Resource");
        Long id = resource.getId();

        mockMvc.perform(delete("/api/resources/" + id)).andExpect(status().isOk());
    }
}
