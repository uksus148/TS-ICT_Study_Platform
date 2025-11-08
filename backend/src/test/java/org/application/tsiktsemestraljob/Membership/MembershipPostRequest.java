package org.application.tsiktsemestraljob.Membership;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.User.UserPostRequest;
import org.application.tsiktsemestraljob.demo.Entities.Membership;
import org.application.tsiktsemestraljob.demo.Entities.Resources;
import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
public class MembershipPostRequest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public Membership postMembership(String role) throws Exception {
        Membership membership = new Membership();
        membership.setRole(role);

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

        String answer = mockMvc.perform(post("/api/memberships/" + id + "/" + groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(membership)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(answer, Membership.class);
    }
}
