package org.application.tsiktsemestraljob.StudyGroups;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.application.tsiktsemestraljob.IntegrationTest;
import org.application.tsiktsemestraljob.User.UserPostRequest;
import org.application.tsiktsemestraljob.demo.DTO.StudyGroupsDTO.StudyGroupsRequestDTO;
import org.application.tsiktsemestraljob.demo.DTO.StudyGroupsDTO.StudyGroupsResponseDTO;
import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;
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
class StudyGroupsIntegrationTest extends IntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private UserPostRequest userPostRequest;
    private StudyGroupsPostRequest studyGroupsPostRequest;

    @BeforeEach
    void setUp() {
        userPostRequest = new UserPostRequest(mockMvc, objectMapper);
        studyGroupsPostRequest = new StudyGroupsPostRequest(mockMvc, objectMapper);
    }

    @Test
    void testGetAllStudyGroups() throws Exception {
        mockMvc.perform(get("/api/studyGroups"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateStudyGroup() throws Exception {
        User userWithId = userPostRequest.postUser("testmail", "testname");
        Long id = userWithId.getId();

        StudyGroups studyGroup = new StudyGroups();
        studyGroup.setName("testgroup");

        mockMvc.perform(post("/api/studyGroups/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studyGroup)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteStudyGroup() throws Exception {
        StudyGroupsPostRequest studyGroupsPostRequest = new StudyGroupsPostRequest(mockMvc, objectMapper);
        StudyGroupsResponseDTO dto = studyGroupsPostRequest.postGroup("testgroup");
        Long id = dto.id();

        mockMvc.perform(delete("/api/studyGroups/" + id))
                .andExpect(status().isOk());

    }

    @Test
    void testUpdateStudyGroup() throws Exception {
        StudyGroupsResponseDTO created = studyGroupsPostRequest.postGroup("oldname");
        Long groupId = created.id();

        StudyGroupsRequestDTO updateDto =
                new StudyGroupsRequestDTO("newname", null);

        mockMvc.perform(put("/api/studyGroups/" + groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("newname"));
    }

    @Test
    void testGetStudyGroupById() throws Exception {
        StudyGroupsResponseDTO dto = studyGroupsPostRequest.postGroup("testgroup");
        Long id = dto.id();

        mockMvc.perform(get("/api/studyGroups/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testgroup"));
    }
}
