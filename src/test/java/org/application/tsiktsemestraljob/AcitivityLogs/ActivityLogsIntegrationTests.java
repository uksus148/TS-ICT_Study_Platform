package org.application.tsiktsemestraljob.AcitivityLogs;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.application.tsiktsemestraljob.IntegrationTest;
import org.application.tsiktsemestraljob.demo.Entities.ActivityLogs;
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
@AutoConfigureMockMvc
@Testcontainers
@Transactional
public class ActivityLogsIntegrationTests extends IntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getActivityLogs() throws Exception {
        mockMvc.perform(get("/api/activitylogs")).andExpect(status().isOk());
    }

    @Test
    void createActivityLogs() throws Exception {
        ActivityLogsPostRequest activityLogsPostRequest = new ActivityLogsPostRequest(mockMvc, objectMapper);
        ActivityLogs activityLog = activityLogsPostRequest.postActivityLog("Test Action");
    }

    @Test
    void getActivityLogById() throws Exception {
        ActivityLogsPostRequest activityLogsPostRequest = new ActivityLogsPostRequest(mockMvc, objectMapper);
        ActivityLogs activityLog = activityLogsPostRequest.postActivityLog("Test Action");

        mockMvc.perform(get("/api/activitylogs/" + activityLog.getId())).andExpect(status().isOk()).andExpect(jsonPath("$.action").value("Test Action"));
    }

    @Test
    void deleteActivityLogById() throws Exception {
        ActivityLogsPostRequest activityLogsPostRequest = new ActivityLogsPostRequest(mockMvc, objectMapper);
        ActivityLogs activityLog = activityLogsPostRequest.postActivityLog("Test Action");

        mockMvc.perform(delete("/api/activitylogs/" + activityLog.getId())).andExpect(status().isOk());
    }

    @Test
    void updateActivityLogById() throws Exception {
        ActivityLogsPostRequest activityLogsPostRequest = new ActivityLogsPostRequest(mockMvc, objectMapper);
        ActivityLogs activityLog = activityLogsPostRequest.postActivityLog("Test Action");
        activityLog.setAction("New action");

        mockMvc.perform(put("/api/activitylogs/" + activityLog.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(activityLog)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.action").value("New action"));
    }
}
