package hexlet.code.app.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.utils.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
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
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ModelGenerator modelGenerator;

    private JwtRequestPostProcessor token;

    private User testUser;

    @BeforeEach
    public void setup() {
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
        testUser = Instancio.of(modelGenerator.getUserModel())
                .create();
        var testUserCreateDto = new UserCreateDTO();
        testUserCreateDto.setFirstName(testUser.getFirstName());
        testUserCreateDto.setLastName(testUser.getLastName());
        testUserCreateDto.setEmail(testUser.getEmail());
        testUserCreateDto.setPassword(testUser.getPassword());
        userRepository.save(userMapper.map(testUserCreateDto));
        testUser = userRepository.findByEmail(testUser.getEmail()).get();
    }

    @Test
    public void indexTest() throws Exception {
        mockMvc.perform(get("/api/users").with(token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void showTest() throws Exception {
        var id = testUser.getId();

        var response = mockMvc.perform(get("/api/users/" + id).with(token))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        assertThat(response.getContentAsString())
                .contains(testUser.getEmail())
                .contains(testUser.getFirstName())
                .contains(testUser.getLastName());

        mockMvc.perform(get("/api/users/" + id))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void createTest() throws Exception {
        var createUser = Instancio.of(modelGenerator.getUserModel())
                .create();
        var testUserCreateDto = new UserCreateDTO();
        testUserCreateDto.setFirstName(createUser.getFirstName());
        testUserCreateDto.setLastName(createUser.getLastName());
        testUserCreateDto.setEmail(createUser.getEmail());
        testUserCreateDto.setPassword(createUser.getPassword());
        createUser = userMapper.map(testUserCreateDto);

        var request = post("/api/users")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(createUser));
        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        var user = userRepository.findByEmail(createUser.getEmail());
        assertThat(user).isNotEmpty();

        createUser = Instancio.of(modelGenerator.getUserModel())
                .create();
        testUserCreateDto.setFirstName(createUser.getFirstName());
        testUserCreateDto.setLastName(createUser.getLastName());
        testUserCreateDto.setEmail("");
        testUserCreateDto.setPassword(createUser.getPassword());
        createUser = userMapper.map(testUserCreateDto);
        request = request
                .content(om.writeValueAsBytes(createUser));
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn();
        user = userRepository.findByEmail(createUser.getEmail());
        assertThat(user).isEmpty();

        mockMvc.perform(post("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateTest() {

    }

    @Test
    public void deleteTest() {

    }
}
