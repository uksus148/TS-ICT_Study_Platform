package org.application.tsiktsemestraljob.Resources;
/**
 * This class created for do an Resources POST request for easier tests implementation
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.User.UserPostRequest;
import org.application.tsiktsemestraljob.demo.Entities.Resources;
import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;
import org.application.tsiktsemestraljob.demo.Entities.Task;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
public class ResourcePostRequest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public Resources postResource(String title) throws Exception {
        Resources resources = new Resources();
        resources.setTitle(title);

        UserPostRequest userPostRequest = new UserPostRequest(mockMvc, objectMapper);
        User user = userPostRequest.postUser("uUser", "uPassword");
        Long id = user.getId();

        StudyGroups studyGroup = new StudyGroups();
        studyGroup.setName("testgroup");

        String response = mockMvc.perform(post("/api/studyGroups/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studyGroup)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        StudyGroups newStudyGroup = objectMapper.readValue(response, StudyGroups.class);
        Long groupId = newStudyGroup.getGroupId();

        String answer = mockMvc.perform(post("/api/resources/" + id + "/" + groupId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resources)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(answer, Resources.class);
    }
}
