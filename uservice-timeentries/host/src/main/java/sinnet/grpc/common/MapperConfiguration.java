package sinnet.grpc.common;

import org.modelmapper.ModelMapper;
import org.modelmapper.protobuf.ProtobufModule;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

@Configurable
class MapperConfiguration {
  
  @Bean
  ModelMapper modelMapper() {
    var mapper = new ModelMapper();
    mapper.registerModule(new ProtobufModule());
    return mapper;
  }
}
