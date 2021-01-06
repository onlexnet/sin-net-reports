package sinnet.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@AllArgsConstructor
@Value
@JsonDeserialize(builder = CustomerSecret.MyBuilder.class)
@Builder(builderClassName = "MyBuilder", toBuilder = true)
public final class CustomerSecret {
    private String location;
    private String username;
    private String password;
    @Builder.Default
    private Email changedWho = Email.empty();
    private LocalDateTime changedWhen;

    @JsonPOJOBuilder(withPrefix = "")
    public static class MyBuilder {
    }
}
