package sinnet.project.events;

import org.apache.avro.Schema;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;

import lombok.SneakyThrows;
import lombok.val;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AvroDeSer {

  /** JSON deserialization. */
  @SneakyThrows
  public static <T> T fromJson(Class<T> clazz, Schema schema, String json) {
    val reader = new SpecificDatumReader<>(clazz);
    // for some reason, currently in 'json' we have additional extra quotation marks. I don't know why
    // but for now let's simply assume they are still there and we are removing them
    val normalizedJson = json.substring(1, json.length());
    val decoder = DecoderFactory.get().jsonDecoder(schema, normalizedJson);
    return reader.read(null, decoder);
  }
}
