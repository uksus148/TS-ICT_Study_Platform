package org.application.tsiktsemestraljob.StudyGroups;
/*
 * This class created for do a StudyGroups POST request for easier tests implementation
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.User.UserRegisterRequest;
import org.application.tsiktsemestraljob.demo.DTO.StudyGroupsDTO.StudyGroupsRequestDTO;
import org.application.tsiktsemestraljob.demo.DTO.StudyGroupsDTO.StudyGroupsResponseDTO;
import org.application.tsiktsemestraljob.demo.DTO.UserDTO.UserResponseDTO;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RequiredArgsConstructor
public class StudyGroupsPostRequest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public StudyGroupsResponseDTO postGroup(String name) throws Exception {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(mockMvc, objectMapper);
        UserResponseDTO user = userRegisterRequest.registeredUser("testname", "testmail", "12345");
        Long id = user.id();

        StudyGroupsRequestDTO dto = new StudyGroupsRequestDTO(
                name,
                null
        );


        String response = mockMvc.perform(post("/api/studyGroups/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(response, StudyGroupsResponseDTO.class);
    }

}
