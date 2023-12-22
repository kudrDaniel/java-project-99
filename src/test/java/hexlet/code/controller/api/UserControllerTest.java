package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ModelGenerator modelGenerator;

    private JwtRequestPostProcessor token;

    private User testUser;

    @BeforeEach
    public void beforeEach() {
        testUser = Instancio.of(modelGenerator.getUserModel())
                .create();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @Test
    void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getUserModel())
                .create();

        var request = post("/api/users")
                .with(token)
                .contentType(APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        var result = mockMvc
                .perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("password").isAbsent(),
                v -> v.node("id").isPresent(),
                v -> v.node("firstName").isEqualTo(data.getFirstName()),
                v -> v.node("lastName").isEqualTo(data.getLastName()),
                v -> v.node("email").isEqualTo(data.getEmail()));
    }

    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/api/users").with(token))
                .andExpect(status().isOk());
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/users/" + testUser.getId()).with(token);
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdate() throws Exception {
        var data = new HashMap<String, String>();
        data.put("firstName", "New name");

        var request = put("/api/users/" + testUser.getId()).with(token)
                .contentType(APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/api/users/" + testUser.getId()).with(token))
                .andExpect(status().isNoContent());
    }
}
