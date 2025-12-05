package org.application.tsiktsemestraljob.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RequiredArgsConstructor
public class UserPostRequest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public User postUser(String name, String emailPrefix) throws Exception {
        User user = new User();
        user.setName(name);
        user.setEmail(emailPrefix + "_" + System.currentTimeMillis() + "@mail.com");
        user.setPasswordHash("123");
        String json = objectMapper.writeValueAsString(user);

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(response, User.class);
    }
}
