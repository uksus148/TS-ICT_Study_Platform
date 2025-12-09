package org.application.tsiktsemestraljob.Task;
/*
 * This class created for do a Task POST request for easier tests implementation
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.User.UserRegisterRequest;
import org.application.tsiktsemestraljob.demo.DTO.StudyGroupsDTO.StudyGroupsResponseDTO;
import org.application.tsiktsemestraljob.demo.DTO.UserDTO.UserResponseDTO;
import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.application.tsiktsemestraljob.demo.DTO.TaskDTO.TaskRequestDTO;
import org.application.tsiktsemestraljob.demo.DTO.TaskDTO.TaskResponseDTO;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
public class TaskPostRequest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public TaskResponseDTO postTask(String title) throws Exception {
        TaskRequestDTO dto = new TaskRequestDTO(
                title,
                null,
                null,
                null
        );

        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(mockMvc, objectMapper);
        UserResponseDTO user = userRegisterRequest.registeredUser("testname", "testmail", "12345");
        Long id = user.id();

        StudyGroups studyGroup = new StudyGroups();
        studyGroup.setName("testgroup");

        String response = mockMvc.perform(post("/api/studyGroups/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studyGroup)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        StudyGroupsResponseDTO dto2 = objectMapper.readValue(response, StudyGroupsResponseDTO.class);
        Long groupId = dto2.id();

        String answer = mockMvc.perform(post("/api/tasks/" + groupId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(answer, TaskResponseDTO.class);
    }
}
