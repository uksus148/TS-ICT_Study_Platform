package org.application.tsiktsemestraljob.AcitivityLogs;
/**
 * This class created for do an ActivityLogs POST request for easier tests implementation
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.User.UserPostRequest;
import org.application.tsiktsemestraljob.demo.Entities.ActivityLogs;
import org.application.tsiktsemestraljob.demo.Entities.Membership;
import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
public class ActivityLogsPostRequest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public ActivityLogs postActivityLog(String action) throws Exception {
        ActivityLogs activityLog = new ActivityLogs();
        activityLog.setAction(action);

        UserPostRequest userPostRequest = new UserPostRequest(mockMvc, objectMapper);
        User user = userPostRequest.postUser("uUser", "uPassword");
        Long id = user.getId();

        String answer = mockMvc.perform(post("/api/activitylogs/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(activityLog)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(answer, ActivityLogs.class);
    }
}
