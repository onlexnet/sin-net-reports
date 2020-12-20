package sinnet.bus.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sinnet.bus.EntityId;
import sinnet.bus.JsonMessage;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCustomerInfo implements JsonMessage {
    /** Address used to send the command to it's consumer. */
    public static final String ADDRESS = "cmd.RegisterNewCustomer";

    private EntityId id;
    private String emailOfOperator;
    private String modelOfSupport;
    private String modelOfBilling;
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
}
