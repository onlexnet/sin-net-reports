package sinnet.domain.model;

import org.mapstruct.Mapper;

import sinnet.models.EntityVersion;
import sinnet.models.ValName;

/** Mapping definition for shared domain models which can be converted to raw single values. */
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

  /**
   * Maps a Long value to an EntityVersion.
   *
   * @param value the Long value to map
   * @return the corresponding EntityVersion
   */
  default EntityVersion map(Long value) {
    if (value == null || value == 0) {
      return EntityVersion.New.INSTANCE;
    }
    return new EntityVersion.Existing(value);
  }
}
