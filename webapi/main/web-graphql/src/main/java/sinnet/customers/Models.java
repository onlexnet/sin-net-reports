package sinnet.customers;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Data
class CustomerInput {
    private String operatorEmail;
    private String billingModel;
    private String supportStatus;
    private Integer distance;
    private String customerName;
    private String customerCityName;
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
}

@Value
@Builder
class CustomerModel {
    private String operatorEmail;
    private String billingModel;
    private String supportStatus;
    private Integer distance;
    private String customerName;
    private String customerCityName;
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
}
@Value
class CustomerSecret {
    private String location;
    private String username;
    private String password;
    private String changedWhen;
    private String changedWho;
}
@Value
class CustomerSecretEx {
    private String location;
    private String username;
    private String password;
    private String entityName;
    private String entityCode;
    private String changedWhen;
    private String changedWho;
}
@Value
class CustomerContact {
    private String name;
    private String surname;
    private String phoneNo;
    private String email;
}
