package hexlet.code.app.exception;

import lombok.Getter;

import static java.lang.String.format;

@Getter
public class EmailAlreadyExistException extends RuntimeException {
    private final String email;

    public EmailAlreadyExistException(String email) {
        super(format("User with email:(%s){0} already exists", email));
        this.email = email;
    }
}
