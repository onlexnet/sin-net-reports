package sinnet.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@AllArgsConstructor
@Value
@JsonDeserialize(builder = CustomerContact.MyBuilder.class)
@Builder(builderClassName = "MyBuilder", toBuilder = true)
public final class CustomerContact {
    private String name;
    private String surname;
    private String phoneNo;
    private String email;

    @JsonPOJOBuilder(withPrefix = "")
    public static class MyBuilder {
    }
}
