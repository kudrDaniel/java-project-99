package hexlet.code.app.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.utils.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TestStatusControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ModelGenerator modelGenerator;

    private JwtRequestPostProcessor token;

    private TaskStatus testTaskStatus;

    private final String rootURI = "/api/task_statuses";

    @BeforeEach
    public void setup() {
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel())
                .create();
        var testTaskStatusCreateDTO = new TaskStatusCreateDTO();
        testTaskStatusCreateDTO.setName(testTaskStatus.getName());
        testTaskStatusCreateDTO.setSlug(testTaskStatus.getSlug());
        taskStatusRepository.save(taskStatusMapper.map(testTaskStatusCreateDTO));
        testTaskStatus = taskStatusRepository.findByName(testTaskStatus.getName()).get();
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
        var id = testTaskStatus.getId();

        var response = mockMvc.perform(get(rootURI + "/" + id).with(token))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        assertThat(response.getContentAsString())
                .contains(testTaskStatus.getName())
                .contains(testTaskStatus.getSlug());

        mockMvc.perform(get(rootURI + "/" + id))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCreate() throws Exception {
        var taskStatus1 = Instancio.of(modelGenerator.getTaskStatusModel())
                .create();
        var taskStatusCreateDTO = new TaskStatusCreateDTO();
        taskStatusCreateDTO.setName(taskStatus1.getName());
        taskStatusCreateDTO.setSlug(taskStatus1.getSlug());

        var request = post(rootURI)
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskStatusCreateDTO));
        mockMvc.perform(request)
                .andExpect(status().isCreated());
        assertThat(taskStatusRepository.existsByName(taskStatusCreateDTO.getName())).isTrue();
        assertThat(taskStatusRepository.existsBySlug(taskStatusCreateDTO.getSlug())).isTrue();

        taskStatusCreateDTO.setName("");
        request = request
                .content(om.writeValueAsString(taskStatusCreateDTO));
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
        assertThat(taskStatusRepository.existsByName(taskStatusCreateDTO.getName())).isFalse();

        mockMvc.perform(post(rootURI))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdate() throws Exception {
        var id = testTaskStatus.getId();

        var taskStatus1 = Instancio.of(modelGenerator.getTaskStatusModel())
                .create();
        var taskStatusUpdateDTO = new TaskStatusUpdateDTO();
        taskStatusUpdateDTO.setName(JsonNullable.of(taskStatus1.getName()));
        taskStatusUpdateDTO.setSlug(JsonNullable.of(taskStatus1.getSlug()));
        var request = put(rootURI + "/" + id)
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskStatusUpdateDTO));
        mockMvc.perform(request)
                .andExpect(status().isOk());
        assertThat(taskStatusRepository.existsByName(taskStatusUpdateDTO.getName().get())).isTrue();
        assertThat(taskStatusRepository.existsBySlug(taskStatusUpdateDTO.getSlug().get())).isTrue();

        var taskStatus2 = Instancio.of(modelGenerator.getTaskStatusModel())
                .create();
        taskStatusUpdateDTO.setName(JsonNullable.of(taskStatus2.getName()));
        taskStatusUpdateDTO.setSlug(JsonNullable.of(null));
        request.content(om.writeValueAsString(taskStatusUpdateDTO));
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
        assertThat(taskStatusRepository.existsByName(taskStatusUpdateDTO.getName().get())).isFalse();

        mockMvc.perform(put(rootURI + "/" + id))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDelete() throws Exception {
        var id = testTaskStatus.getId();

        mockMvc.perform(delete(rootURI + "/" + id))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete(rootURI + "/" + id).with(token))
                .andExpect(status().isOk());
    }
}
