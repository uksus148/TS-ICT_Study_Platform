package org.application.tsiktsemestraljob.Membership;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.application.tsiktsemestraljob.IntegrationTest;
import org.application.tsiktsemestraljob.demo.Entities.Membership;
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
public class MembershipIntegrationTest  extends IntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getMemberships() throws Exception {
        mockMvc.perform(get("/api/memberships")).andExpect(status().isOk());
    }

    @Test
    void createMembership() throws Exception {
        MembershipPostRequest membershipPostRequest = new MembershipPostRequest(mockMvc, objectMapper);
        Membership membership = membershipPostRequest.postMembership("Test Role");
    }

    @Test
    void updateMembership() throws Exception {
        MembershipPostRequest membershipPostRequest = new MembershipPostRequest(mockMvc, objectMapper);
        Membership membership = membershipPostRequest.postMembership("Test Role");

        membership.setRole("Another Role");

        mockMvc.perform(put("/api/memberships/" + membership.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(membership)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("Another Role"));
    }

    @Test
    void deleteMembership() throws Exception {
        MembershipPostRequest membershipPostRequest = new MembershipPostRequest(mockMvc, objectMapper);
        Membership membership = membershipPostRequest.postMembership("Test Role");
        mockMvc.perform(delete("/api/memberships/" + membership.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void getMembershipById() throws Exception {
        MembershipPostRequest membershipPostRequest = new MembershipPostRequest(mockMvc, objectMapper);
        Membership membership = membershipPostRequest.postMembership("Test Role");
        mockMvc.perform(get("/api/memberships/" + membership.getId()))
                .andExpect(status().isOk());
    }
}
