package sinnet.gql.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerMapper Tests")
class CustomerMapperTest {

  private final sinnet.infra.adapters.gql.CustomerMapper mapper = sinnet.infra.adapters.gql.CustomerMapper.INSTANCE;

}
