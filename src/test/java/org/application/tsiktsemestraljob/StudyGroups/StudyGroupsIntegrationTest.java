package org.application.tsiktsemestraljob.StudyGroups;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.application.tsiktsemestraljob.IntegrationTest;
import org.application.tsiktsemestraljob.User.UserRegisterRequest;
import org.application.tsiktsemestraljob.demo.DTO.StudyGroupsDTO.StudyGroupsRequestDTO;
import org.application.tsiktsemestraljob.demo.DTO.StudyGroupsDTO.StudyGroupsResponseDTO;
import org.application.tsiktsemestraljob.demo.DTO.UserDTO.UserResponseDTO;
import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.application.tsiktsemestraljob.demo.Enums.MembershipRole;
import org.application.tsiktsemestraljob.demo.Repository.StudyGroupsRepository;
import org.application.tsiktsemestraljob.demo.Repository.UserRepository;
import org.application.tsiktsemestraljob.demo.Service.MembershipService;
import org.application.tsiktsemestraljob.demo.Service.StudyGroupsService;
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
@WithMockUser(username = "testmail")
class StudyGroupsIntegrationTest extends IntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudyGroupsService studyGroupsService;

    @Autowired
    private StudyGroupsRepository studyGroupsRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MembershipService membershipService;

    private UserRegisterRequest userRegisterRequest;
    private StudyGroupsPostRequest studyGroupsPostRequest;
    @BeforeEach
    void setUp() {
        userRegisterRequest = new UserRegisterRequest(mockMvc, objectMapper);
        studyGroupsPostRequest = new StudyGroupsPostRequest(mockMvc, objectMapper);
    }

    @Test
    void testGetAllStudyGroups() throws Exception {
        mockMvc.perform(get("/api/studyGroups"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateStudyGroup() throws Exception {
        StudyGroupsResponseDTO studyGroups = studyGroupsPostRequest.postGroup("testgroup");
        Long id = studyGroups.id();

        mockMvc.perform(get("/api/studyGroups/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testgroup"));
    }

    @Test
    void testDeleteStudyGroup() throws Exception {
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
