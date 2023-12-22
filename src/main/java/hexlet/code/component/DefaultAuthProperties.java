package hexlet.code.component;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "defauth")
@Getter
@Setter
public class DefaultAuthProperties {
    private String email;
    private String password;
}
