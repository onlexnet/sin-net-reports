package sinnet.gql.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import java.time.LocalDateTime;

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import sinnet.domain.models.CustomerContact;
import sinnet.domain.models.CustomerEntry;
import sinnet.domain.models.CustomerSecret;
import sinnet.domain.models.CustomerSecretEx;
import sinnet.gql.models.CustomerContactInputGql;
import sinnet.gql.models.CustomerInput;
import sinnet.gql.models.CustomerSecretExInput;
import sinnet.gql.models.CustomerSecretInput;
import sinnet.gql.models.EntityGql;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerMapper Tests")
class CustomerMapperTest {

  private final sinnet.infra.adapters.gql.CustomerMapper mapper = sinnet.infra.adapters.gql.CustomerMapper.INSTANCE;

}
