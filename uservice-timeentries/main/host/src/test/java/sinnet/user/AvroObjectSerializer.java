package sinnet.user;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.BinaryMessageEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.dapr.serializer.DaprObjectSerializer;
import io.dapr.utils.TypeRef;
import lombok.SneakyThrows;

/** Not optimized ser/deser component for Avro generated classes. */
final public class AvroObjectSerializer implements DaprObjectSerializer {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Override
  @SneakyThrows
  public byte[] serialize(Object o) throws IOException {
    var clazz = o.getClass();
    // reusing well-known method generated by Avro compiler
    var encoderMethod = clazz.getMethod("getEncoder", null);
    var a = (BinaryMessageEncoder<Object>) encoderMethod.invoke(o);
    ByteBuffer b = a.encode(o);
    return b.array();
  }

  @Override
  @SneakyThrows
  public <T> T deserialize(byte[] data, TypeRef<T> type) throws IOException {
    var clazz = (Class<T>) type.getType();
    var decoderMethod = clazz.getMethod("getDecoder", null);
    var a = (BinaryMessageDecoder<Object>) decoderMethod.invoke(null);
    return (T) a.decode(data);
  }

  @Override
  public String getContentType() {
    return "application/json";
  }

}