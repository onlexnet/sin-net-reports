package sinnet.gql.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import graphql.language.StringValue;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

class DateScalarTest {

  private DateScalar dateScalar;

  @BeforeEach
  void setUp() {
    dateScalar = new DateScalar();
  }

  @Test
  void testSerializeValidLocalDate() {
    // Arrange
    var date = LocalDate.of(2023, 12, 25);

    // Act
    var result = dateScalar.serialize(date);

    // Assert
    assertEquals("2023-12-25", result);
  }

  @Test
  void testSerializeInvalidObject() {
    // Arrange
    var invalidObject = "not-a-date";

    // Act & Assert
    assertThrows(CoercingSerializeException.class, () -> {
      dateScalar.serialize(invalidObject);
    });
  }

  @Test
  void testParseValueValidString() {
    // Arrange
    var dateString = "2023-12-25";

    // Act
    var result = dateScalar.parseValue(dateString);

    // Assert
    assertEquals(LocalDate.of(2023, 12, 25), result);
  }

  @Test
  void testParseValueInvalidString() {
    // Arrange
    var invalidDateString = "invalid-date";

    // Act & Assert
    assertThrows(CoercingParseValueException.class, () -> {
      dateScalar.parseValue(invalidDateString);
    });
  }

  @Test
  void testParseValueInvalidType() {
    // Arrange
    var invalidObject = 12345;

    // Act & Assert
    assertThrows(CoercingParseValueException.class, () -> {
      dateScalar.parseValue(invalidObject);
    });
  }

  @Test
  void testParseLiteralValidStringValue() {
    // Arrange
    var stringValue = StringValue.newStringValue("2023-12-25").build();

    // Act
    var result = dateScalar.parseLiteral(stringValue);

    // Assert
    assertEquals(LocalDate.of(2023, 12, 25), result);
  }

  @Test
  void testParseLiteralInvalidStringValue() {
    // Arrange
    var stringValue = StringValue.newStringValue("invalid-date").build();

    // Act & Assert
    assertThrows(CoercingParseLiteralException.class, () -> {
      dateScalar.parseLiteral(stringValue);
    });
  }

  @Test
  void testParseLiteralInvalidType() {
    // Arrange
    var invalidLiteral = "not-a-string-value";

    // Act & Assert
    assertThrows(CoercingParseLiteralException.class, () -> {
      dateScalar.parseLiteral(invalidLiteral);
    });
  }

  @Test
  void testNewScalar() {
    // Act
    var scalar = DateScalar.newScalar();

    // Assert
    assertNotNull(scalar);
    assertEquals("Date", scalar.getName());
    assertNotNull(scalar.getCoercing());
  }
}