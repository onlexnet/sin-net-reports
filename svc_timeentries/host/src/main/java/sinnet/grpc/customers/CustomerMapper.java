package sinnet.grpc.customers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import sinnet.domain.model.DomainMapper;
import sinnet.models.EntityVersion;

/** Dbo <-> domain mapper. */
@Mapper(
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = { DomainMapper.class })
public interface CustomerMapper {

  CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

  default Long fromVersion(EntityVersion source) {
    return EntityVersion.toDbo(source);
  }


  @Mapping(target = "entityId", source = "id.id")
  @Mapping(source = "id.version", target = "entityVersion")
  @Mapping(source = "id.projectId", target = "projectId")
  @Mapping(source = "value.billingModel", target = "billingModel")
  @Mapping(source = "value.customerAddress", target = "customerAddress")
  @Mapping(source = "value.customerCityName", target = "customerCityName")
  @Mapping(source = "value.customerName", target = "customerName")
  @Mapping(source = "value.daneTechniczne", target = "daneTechniczne")
  @Mapping(source = "value.distance", target = "distance")
  @Mapping(source = "value.komercjaJest", target = "komercjaJest")
  @Mapping(source = "value.komercjaNotatki", target = "komercjaNotatki")
  @Mapping(source = "value.nfzAmbulatoryjnaOpiekaSpecjalistyczna", target = "nfzAmbulatoryjnaOpiekaSpecjalistyczna")
  @Mapping(source = "value.nfzLekarz", target = "nfzLekarz")
  @Mapping(source = "value.nfzMaFilie", target = "nfzMaFilie")
  @Mapping(source = "value.nfzMedycynaSzkolna", target = "nfzMedycynaSzkolna")
  @Mapping(source = "value.nfzNocnaPomocLekarska", target = "nfzNocnaPomocLekarska")
  @Mapping(source = "value.nfzNotatki", target = "nfzNotatki")
  @Mapping(source = "value.nfzOpiekaDlugoterminowa", target = "nfzOpiekaDlugoterminowa")
  @Mapping(source = "value.nfzPielegniarkaSrodowiskowa", target = "nfzPielegniarkaSrodowiskowa")
  @Mapping(source = "value.nfzPolozna", target = "nfzPolozna")
  @Mapping(source = "value.nfzProgramyProfilaktyczne", target = "nfzProgramyProfilaktyczne")
  @Mapping(source = "value.nfzPsychiatria", target = "nfzPsychiatria")
  @Mapping(source = "value.nfzRehabilitacja", target = "nfzRehabilitacja")
  @Mapping(source = "value.nfzStomatologia", target = "nfzStomatologia")
  @Mapping(source = "value.nfzSzpitalnictwo", target = "nfzSzpitalnictwo")
  @Mapping(source = "value.nfzTransportSanitarny", target = "nfzTransportSanitarny")
  @Mapping(source = "value.nfzUmowa", target = "nfzUmowa")
  @Mapping(source = "value.nfzZaopatrzenieOrtopedyczne", target = "nfzZaopatrzenieOrtopedyczne")
  @Mapping(source = "value.operatorEmail", target = "operatorEmail")
  @Mapping(source = "value.supportStatus", target = "supportStatus")
  CustomerRepository.CustomerDbo toJpaDbo(sinnet.models.CustomerModel model);

  @Mapping(target = "id.id", source = "entityId")
  @Mapping(target = "id.version", source = "entityVersion")
  @Mapping(target = "id.projectId", source = "projectId")
  @Mapping(target = "value.billingModel", source = "billingModel")
  @Mapping(target = "value.customerAddress", source = "customerAddress")
  @Mapping(target = "value.customerCityName", source = "customerCityName")
  @Mapping(target = "value.customerName", source = "customerName")
  @Mapping(target = "value.daneTechniczne", source = "daneTechniczne")
  @Mapping(target = "value.distance", source = "distance")
  @Mapping(target = "value.komercjaJest", source = "komercjaJest")
  @Mapping(target = "value.komercjaNotatki", source = "komercjaNotatki")
  @Mapping(target = "value.nfzAmbulatoryjnaOpiekaSpecjalistyczna", source = "nfzAmbulatoryjnaOpiekaSpecjalistyczna")
  @Mapping(target = "value.nfzLekarz", source = "nfzLekarz")
  @Mapping(target = "value.nfzMaFilie", source = "nfzMaFilie")
  @Mapping(target = "value.nfzMedycynaSzkolna", source = "nfzMedycynaSzkolna")
  @Mapping(target = "value.nfzNocnaPomocLekarska", source = "nfzNocnaPomocLekarska")
  @Mapping(target = "value.nfzNotatki", source = "nfzNotatki")
  @Mapping(target = "value.nfzOpiekaDlugoterminowa", source = "nfzOpiekaDlugoterminowa")
  @Mapping(target = "value.nfzPielegniarkaSrodowiskowa", source = "nfzPielegniarkaSrodowiskowa")
  @Mapping(target = "value.nfzPolozna", source = "nfzPolozna")
  @Mapping(target = "value.nfzProgramyProfilaktyczne", source = "nfzProgramyProfilaktyczne")
  @Mapping(target = "value.nfzPsychiatria", source = "nfzPsychiatria")
  @Mapping(target = "value.nfzRehabilitacja", source = "nfzRehabilitacja")
  @Mapping(target = "value.nfzStomatologia", source = "nfzStomatologia")
  @Mapping(target = "value.nfzSzpitalnictwo", source = "nfzSzpitalnictwo")
  @Mapping(target = "value.nfzTransportSanitarny", source = "nfzTransportSanitarny")
  @Mapping(target = "value.nfzUmowa", source = "nfzUmowa")
  @Mapping(target = "value.nfzZaopatrzenieOrtopedyczne", source = "nfzZaopatrzenieOrtopedyczne")
  @Mapping(target = "value.operatorEmail", source = "operatorEmail")
  @Mapping(target = "value.supportStatus", source = "supportStatus")
  sinnet.models.CustomerModel fromDbo2(CustomerRepository.CustomerDbo dbo);
  
}
