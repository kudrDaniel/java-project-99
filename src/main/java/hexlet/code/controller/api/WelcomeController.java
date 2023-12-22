package hexlet.code.controller.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public final class WelcomeController {
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to Spring";
    }
}
