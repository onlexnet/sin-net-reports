package sinnet.customer;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@AllArgsConstructor
@Value
@JsonDeserialize(builder = AuthTracker.MyBuilder.class)
@Builder(builderClassName = "MyBuilder", toBuilder = true)
public class AuthTracker {
    private String who;
    private LocalDateTime when;

    @JsonPOJOBuilder(withPrefix = "")
    public static class MyBuilder {
    }
}
