package hexlet.code.app.exception;

import static java.lang.String.format;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Class<?> resourceClass, Long resourceId) {
        super(format("(%s){0} with id:(%s){1} not found", resourceClass.getSimpleName(), resourceId));
    }
}
