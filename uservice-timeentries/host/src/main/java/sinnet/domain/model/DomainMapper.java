package sinnet.domain.model;

import org.mapstruct.Mapper;

import sinnet.models.ValName;

/** Mapping definition for shared domain models. */
@Mapper
public interface DomainMapper {
  
  default ValEmail fromEmail(String dbo) {
    return ValEmail.of(dbo);
  }

  default String fromEmail(ValEmail source) {
    return source.value();
  }

  default ValName fromName(String dbo) {
    return ValName.of(dbo);
  }

  default String fromName(ValName source) {
    return source.getValue();
  }
}
