package hexlet.code.app.exception;


import hexlet.code.app.model.BaseEntity;

import static java.lang.String.format;

public class RemovalConflictException extends RuntimeException {
    public RemovalConflictException(
            Class<? extends BaseEntity> resourceClassToRemove,
            Class<? extends BaseEntity> resourceClassCauseRemove
    ) {
        super(format(
                "Can't remove %s that has %s",
                resourceClassToRemove.getSimpleName(),
                resourceClassCauseRemove.getSimpleName()
        ));
    }
}
