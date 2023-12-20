package hexlet.code.app.component;

import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.service.TaskStatusService;
import hexlet.code.app.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {
    @Autowired
    private final UserService userService;

    @Autowired
    private final TaskStatusService taskStatusService;

    @Autowired
    private final DefaultAuthProperties auths;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initUsers();
        initTaskStatuses();
    }

    private void initUsers() {
        var userData = new UserCreateDTO();
        userData.setEmail(auths.getEmail());
        userData.setPassword(auths.getPassword());
        userService.create(userData);
    }

    private void initTaskStatuses() {
        Map<String, TaskStatusCreateDTO> taskStatusMap = Map.of(
                "draft", new TaskStatusCreateDTO(),
                "to_review", new TaskStatusCreateDTO(),
                "to_be_fixed", new TaskStatusCreateDTO(),
                "to_publish", new TaskStatusCreateDTO(),
                "published", new TaskStatusCreateDTO()
        );
        taskStatusMap.entrySet().stream()
                .map(entry -> {
                    var taskStatus = entry.getValue();
                    taskStatus.setName(entry.getKey());
                    taskStatus.setSlug(entry.getKey());
                    return taskStatus;
                })
                .forEach(taskStatusService::create);
    }
}
