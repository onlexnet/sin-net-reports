package sinnet.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@AllArgsConstructor
@Value
@Jacksonized
@Builder(toBuilder = true)
public final class CustomerSecretEx {
    private String location;
    private String username;
    private String password;
    private String entityName;
    private String entityCode;

    @Builder.Default
    private Email changedWho = Email.empty();
    private LocalDateTime changedWhen;
}
