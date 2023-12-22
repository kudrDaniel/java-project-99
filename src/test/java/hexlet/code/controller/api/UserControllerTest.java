package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    private final String rootURI = "/api/users";

    @BeforeEach
    public void setup() {
        testUser = Instancio.of(modelGenerator.getUserModel())
                .create();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @ParameterizedTest
    @CsvSource(value = {
        "true, 200",
        "false, 401"
    }, delimiter = ',')
    public void testIndex(String withToken, String statusCode) throws Exception {
        var status = status().is(Integer.parseInt(statusCode));
        var request = get(rootURI);
        if (withToken.equals("true")) {
            request = request.with(token);
        }

        mockMvc.perform(request)
                .andExpect(status);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "true, 200",
        "false, 401"
    }, delimiter = ',')
    public void testShow(String withToken, String statusCode) throws Exception {
        var status = status().is(Integer.parseInt(statusCode));
        var request = get(rootURI + "/" + testUser.getId());
        if (withToken.equals("true")) {
            request = request.with(token);
        }

        var response = mockMvc.perform(request)
                .andExpect(status)
                .andReturn().getResponse();

        if (withToken.equals("true")) {
            var body = response.getContentAsString();
            assertThatJson(body).and(
                    v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                    v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                    v -> v.node("email").isEqualTo(testUser.getEmail())
            );
        }
    }

    @ParameterizedTest
    @CsvSource(value = {
        "true, new, 201",
        "true, test, 400",
        "false, new, 401",
    })
    public void testCreate(String withToken, String createType, String statusCode) throws Exception {
        var status = status().is(Integer.parseInt(statusCode));

        var user = Instancio.of(modelGenerator.getUserModel())
                .create();
        if (createType.equals("test")) {
            user = testUser;
        }
        var request = post(rootURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(user));
        if (withToken.equals("true")) {
            request = request.with(token);
        }

        var response = mockMvc.perform(request)
                .andExpect(status)
                .andReturn().getResponse();

        if (withToken.equals("true") && createType.equals("new")) {
            var body = response.getContentAsString();
            var firstName = user.getFirstName();
            var lastName = user.getLastName();
            var email = user.getEmail();
            assertThatJson(body).and(
                    v -> v.node("firstName").isEqualTo(firstName),
                    v -> v.node("lastName").isEqualTo(lastName),
                    v -> v.node("email").isEqualTo(email)
            );
        }
    }

    @Test
    public void testUpdate() {

    }

    @Test
    public void testDelete() {

    }
}
