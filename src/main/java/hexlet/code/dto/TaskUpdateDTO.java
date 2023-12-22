package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

@Getter
@Setter
public class TaskUpdateDTO {
    @NotBlank
    private JsonNullable<String> title;

    private JsonNullable<Integer> index;

    private JsonNullable<String> content;

    @NotBlank
    private JsonNullable<String> status;

    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;

    private JsonNullable<List<Long>> taskLabelIds;

    @JsonSetter("name")
    public void setTheName(@NotBlank JsonNullable<String> name) {
        this.title = name;
    }
}
