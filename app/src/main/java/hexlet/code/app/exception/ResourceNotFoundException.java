package hexlet.code.app.exception;

import hexlet.code.app.model.BaseEntity;

import static java.lang.String.format;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Class<? extends BaseEntity> resourceClass, Long resourceId) {
        super(format("%s with id:%s not found", resourceClass.getSimpleName(), resourceId));
    }

    public ResourceNotFoundException(
            Class<? extends BaseEntity> resourceClass,
            String resourceName,
            String resourceId
    ) {
        super(format("%s with %s:%s not found", resourceClass.getSimpleName(), resourceName, resourceId));
    }
}
