package sinnet.models;

import lombok.Data;
import lombok.experimental.Accessors;
import sinnet.domain.model.ValEmail;

/**
 * TBD.
 */
@Data
@Accessors(chain = true)
public final class CustomerValue {
  private ValEmail operatorEmail = ValEmail.empty();
  private String billingModel;
  private String supportStatus;
  private int distance;
  private ValName customerName = ValName.empty();
  private ValName customerCityName = ValName.empty();
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
  private String daneTechniczne;
}
