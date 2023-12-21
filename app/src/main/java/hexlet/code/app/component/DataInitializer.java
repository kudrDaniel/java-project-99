package hexlet.code.app.component;

import hexlet.code.app.model.Label;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final TaskStatusRepository taskStatusRepository;

    @Autowired
    private final LabelRepository labelRepository;

    @Autowired
    private final DefaultAuthProperties auths;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initUsers();
        initTaskStatuses();
        initLabels();
    }

    private void initUsers() {
        if (userRepository.existsByEmail(auths.getEmail())) {
            return;
        }
        var user = new User();
        user.setEmail(auths.getEmail());
        var hashedPassword = passwordEncoder.encode(auths.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    private void initTaskStatuses() {
        Map<String, TaskStatus> taskStatusMap = Map.of(
                "draft", new TaskStatus(),
                "to_review", new TaskStatus(),
                "to_be_fixed", new TaskStatus(),
                "to_publish", new TaskStatus(),
                "published", new TaskStatus()
        );
        taskStatusMap.entrySet().stream()
                .map(entry -> {
                    var taskStatus = entry.getValue();
                    taskStatus.setName(entry.getKey());
                    taskStatus.setSlug(entry.getKey());
                    return taskStatus;
                })
                .filter(element -> {
                    var nameCond = taskStatusRepository.existsByName(element.getName());
                    var slugCond = taskStatusRepository.existsBySlug(element.getSlug());
                    return !(nameCond || slugCond);
                })
                .sorted(((o1, o2) -> o1.getName().compareTo(o2.getName())))
                .forEach(taskStatusRepository::save);
    }

    private void initLabels() {
        Map<String, Label> labelMap = Map.of(
                "bug", new Label(),
                "feature", new Label()
        );
        labelMap.entrySet().stream()
                .map(entry -> {
                    var label = entry.getValue();
                    label.setName(entry.getKey());
                    return label;
                })
                .filter(element -> !labelRepository.existsByName(element.getName()))
                .sorted(((o1, o2) -> o1.getName().compareTo(o2.getName())))
                .forEach(labelRepository::save);
    }
}
