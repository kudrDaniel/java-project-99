package hexlet.code.app.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.component.DefaultAuthProperties;
import hexlet.code.app.dto.AuthRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DefaultAuthProperties auth;

    @Autowired
    private ObjectMapper om;

    @Test
    public void loginOkTest() throws Exception {
        var data = new AuthRequest();
        data.setUsername(auth.getEmail());
        data.setPassword(auth.getPassword());

        var request = post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        var content = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        assertThat(content).isNotBlank();
    }

    @ParameterizedTest()
    @CsvSource(delimiter = ':', value = {
            ":",
            "somebody@wants.told.me:the world is gonna roll me",
            "hexlet@example.com:asdfg"
    })
    public void loginInvalidDataTest(String email, String password) throws Exception {
        var data = new AuthRequest();
        data.setUsername(email);
        data.setPassword(password);

        var request = post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }
}
