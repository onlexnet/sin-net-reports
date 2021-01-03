package sinnet.models;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@AllArgsConstructor
@Value
@JsonDeserialize(builder = CustomerSecretEx.MyBuilder.class)
@Builder(builderClassName = "MyBuilder", toBuilder = true)
public final class CustomerSecretEx {
    private String location;
    private String username;
    private String password;
    private String entityName;
    private String entityCode;

    @Builder.Default
    private Email changedWho = Email.empty();
    private LocalDate changedWhen;

    @JsonPOJOBuilder(withPrefix = "")
    public static class MyBuilder {
    }
}
