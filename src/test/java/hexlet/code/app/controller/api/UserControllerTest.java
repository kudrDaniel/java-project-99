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

    private final String rootURI = "/api/users";

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
    public void testIndex() throws Exception {
        mockMvc.perform(get(rootURI).with(token))
                .andExpect(status().isOk());

        mockMvc.perform(get(rootURI))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testShow() throws Exception {
        var id = testUser.getId();

        var response = mockMvc.perform(get(rootURI + "/" + id).with(token))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        assertThat(response.getContentAsString())
                .contains(testUser.getEmail())
                .contains(testUser.getFirstName())
                .contains(testUser.getLastName());

        mockMvc.perform(get(rootURI + "/" + id))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCreate() throws Exception {
        var user1 = Instancio.of(modelGenerator.getUserModel())
                .create();
        var user1CreateDTO = new UserCreateDTO();
        user1CreateDTO.setFirstName(user1.getFirstName());
        user1CreateDTO.setLastName(user1.getLastName());
        user1CreateDTO.setEmail(user1.getEmail());
        user1CreateDTO.setPassword(user1.getPassword());
        var request = post(rootURI)
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(user1CreateDTO));
        mockMvc.perform(request)
                .andExpect(status().isCreated());
        var user = userRepository.findByEmail(user1.getEmail());
        assertThat(user).isNotEmpty();

        user1 = Instancio.of(modelGenerator.getUserModel())
                .create();
        user1CreateDTO.setFirstName(user1.getFirstName());
        user1CreateDTO.setLastName(user1.getLastName());
        user1CreateDTO.setEmail("");
        user1CreateDTO.setPassword(user1.getPassword());
        request = request
                .content(om.writeValueAsBytes(user1CreateDTO));
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
        user = userRepository.findByEmail(user1.getEmail());
        assertThat(user).isEmpty();

        mockMvc.perform(post(rootURI))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdate() {

    }

    @Test
    public void testDelete() {

    }
}
