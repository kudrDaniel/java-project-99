package hexlet.code.app.component;

import hexlet.code.app.model.User;
import hexlet.code.app.service.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {
    @Autowired
    private final CustomUserDetailsService userService;

    @Autowired
    private final DefaultAuthProperties auths;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var userData = new User();
        userData.setEmail(auths.getEmail());
        userData.setPassword(auths.getPassword());
        userService.createUser(userData);
    }
}
