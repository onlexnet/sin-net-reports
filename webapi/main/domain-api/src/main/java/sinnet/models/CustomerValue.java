package sinnet.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@AllArgsConstructor
@Value
@JsonDeserialize(builder = CustomerValue.MyBuilder.class)
@Builder(builderClassName = "MyBuilder", toBuilder = true)
public final class CustomerValue implements EntityValue<CustomerValue> {
    private String operatorEmail;
    private String billingModel;
    private String supportStatus;
    private Integer distance;
    @Builder.Default
    private Name customerName = Name.empty();
    @Builder.Default
    private Name customerCityName = Name.empty();
    private String customerAddress;
    private boolean nfzUmowa;
    private boolean nfzMaFilie;
    private boolean nfzLekarz;
    private boolean nfzPolozna;
    private boolean nfzPielegniarkaSrodowiskowa;
    private boolean nfzMedycynaSzkolna;
    private boolean nfzTransportSanitarny;
    private boolean nfzNocnaPomocLekarska;
    private boolean nfzAmbulatoryjnaOpiekaSpecjalistyczna;
    private boolean nfzRehabilitacja;
    private boolean nfzStomatologia;
    private boolean nfzPsychiatria;
    private boolean nfzSzpitalnictwo;
    private boolean nfzProgramyProfilaktyczne;
    private boolean nfzZaopatrzenieOrtopedyczne;
    private boolean nfzOpiekaDlugoterminowa;
    private String nfzNotatki;
    private boolean komercjaJest;
    private String komercjaNotatki;

    @JsonPOJOBuilder(withPrefix = "")
    public static class MyBuilder {
    }
}
