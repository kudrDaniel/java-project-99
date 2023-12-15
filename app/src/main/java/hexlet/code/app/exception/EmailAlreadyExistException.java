package hexlet.code.app.exception;

import lombok.Getter;

@Getter
public class EmailAlreadyExistException extends RuntimeException {
    private final String email;

    public EmailAlreadyExistException(String email) {
        super(String.format("User with email:(%s){0} already exists", email));
        this.email = email;
    }
}
