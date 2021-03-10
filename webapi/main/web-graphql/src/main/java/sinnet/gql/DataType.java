package sinnet.gql;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

/** Implementation for Date scalar type used in GQL. */
@Configuration
class DateType {

  /**
   * Adds Date type to GraphQL semantic.
   *
   * @return GQL Date type converter to LocalDate
   */
  @Bean
  public GraphQLScalarType dateScalarType() {
    return GraphQLScalarType
          .newScalar()
          .name("Date")
          .description("Date value without time")
          .coercing(new LocalDateCoercing())
          .build();
  }

  /** Allows to operate on new type for GQL: Date. */
  static final class LocalDateCoercing implements Coercing<LocalDate, String> {

    /** The only one supported format of Date. */
    private static DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /**
     * Serializes LocalDate to String.
     *
     * @param input LocalDate to serialize
     * @return serialized value
     * @throws CoercingSerializeException if value input can't be serialized
     */
    @Override
    public String serialize(Object input) throws CoercingSerializeException {
      var typedInput = (LocalDate) input;
      return typedInput.format(pattern);
    }

    /**
     * Parses provided input as a string and returns LocalDate.
     *
     * @param input object that will be parsed to LocalDate
     * @return Parsed LocalDate
     * @throws CoercingParseValueException if value input can't be parsed
     */
    @Override
    public LocalDate parseValue(Object input) throws CoercingParseValueException {
      var inputAsString = input.toString();
      return LocalDate.parse(inputAsString, pattern);
    }

    /**
     * Parses provided StringValue input and returns LocalDate.
     *
     * @param input object that will be parsed to LocalDate
     * @return Parsed LocalDate
     * @throws CoercingParseLiteralException if value input can't be parsed
     */
    @Override
    public LocalDate parseLiteral(Object input) throws CoercingParseLiteralException {
      var inputAsString = ((StringValue) input).getValue();
      return LocalDate.parse(inputAsString, pattern);
    }
  }
}
