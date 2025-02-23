package sinnet.models;

import org.mapstruct.Mapper;
import org.mapstruct.control.DeepClone;
import org.mapstruct.factory.Mappers;

import sinnet.domain.model.ValEmail;

/**
 * Depe copy of domain models.
 */
@Mapper(mappingControl = DeepClone.class)
public interface Clone {
  
  Clone INSTANCE = Mappers.getMapper(Clone.class);
  
  CustomerSecret of(CustomerSecret source);

  CustomerSecretEx of(CustomerSecretEx source);

  default ValEmail of(ValEmail source) {
    return source;
  }

}
