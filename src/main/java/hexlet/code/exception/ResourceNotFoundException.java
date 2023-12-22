package hexlet.code.exception;

import hexlet.code.model.BaseEntity;

import static java.lang.String.format;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(
            Class<? extends BaseEntity> resourceClass,
            String resourceName,
            Object resourceId
    ) {
        super(format("%s with %s:%s not found", resourceClass.getSimpleName(), resourceName, resourceId));
    }
}
