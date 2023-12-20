package hexlet.code.app.exception;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Getter
public class TaskStatusAlreadyExistException extends RuntimeException {
    private final Map<String, String> fields;

    public TaskStatusAlreadyExistException(Map<String, String> fields) {
        super(format("Task status with [%s] already exist", prepareMessage(fields)));
        this.fields = Map.copyOf(fields);
    }

    private static String prepareMessage(Map<String, String> fields) {
        return fields.entrySet().stream()
                .sorted((o1, o2) -> o1.getKey().compareTo(o2.getKey()))
                .map(entry -> format("%s:%s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(", "));
    }
}
