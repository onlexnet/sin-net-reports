package sinnet.gql.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import graphql.language.StringValue;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

class BigIntegerScalarTest {

  private BigIntegerScalar bigIntegerScalar;

  @BeforeEach
  void setUp() {
    bigIntegerScalar = new BigIntegerScalar();
  }

  @Test
  void testSerializeValidBigInteger() {
    // Arrange
    var bigInt = new BigInteger("123456789012345678901234567890");

    // Act
    var result = bigIntegerScalar.serialize(bigInt);

    // Assert
    assertEquals("123456789012345678901234567890", result);
  }

  @Test
  void testSerializeInvalidObject() {
    // Arrange
    var invalidObject = "not-a-biginteger";

    // Act & Assert
    assertThrows(CoercingSerializeException.class, () -> {
      bigIntegerScalar.serialize(invalidObject);
    });
  }

  @Test
  void testParseValueValidString() {
    // Arrange
    var bigIntString = "123456789012345678901234567890";

    // Act
    var result = bigIntegerScalar.parseValue(bigIntString);

    // Assert
    assertEquals(new BigInteger("123456789012345678901234567890"), result);
  }

  @Test
  void testParseValueInvalidString() {
    // Arrange
    var invalidBigIntString = "not-a-number";

    // Act & Assert
    assertThrows(NumberFormatException.class, () -> {
      bigIntegerScalar.parseValue(invalidBigIntString);
    });
  }

  @Test
  void testParseValueInvalidType() {
    // Arrange
    var invalidObject = 12345;

    // Act & Assert
    assertThrows(CoercingParseValueException.class, () -> {
      bigIntegerScalar.parseValue(invalidObject);
    });
  }

  @Test
  void testParseLiteralValidStringValue() {
    // Arrange
    var stringValue = StringValue.newStringValue("123456789012345678901234567890").build();

    // Act
    var result = bigIntegerScalar.parseLiteral(stringValue);

    // Assert
    assertEquals(new BigInteger("123456789012345678901234567890"), result);
  }

  @Test
  void testParseLiteralInvalidStringValue() {
    // Arrange
    var stringValue = StringValue.newStringValue("not-a-number").build();

    // Act & Assert
    assertThrows(NumberFormatException.class, () -> {
      bigIntegerScalar.parseLiteral(stringValue);
    });
  }

  @Test
  void testParseLiteralInvalidType() {
    // Arrange
    var invalidLiteral = "not-a-string-value";

    // Act & Assert
    assertThrows(CoercingParseLiteralException.class, () -> {
      bigIntegerScalar.parseLiteral(invalidLiteral);
    });
  }

  @Test
  void testNewScalar() {
    // Act
    var scalar = BigIntegerScalar.newScalar();

    // Assert
    assertNotNull(scalar);
    assertEquals("BigInteger", scalar.getName());
    assertNotNull(scalar.getCoercing());
  }
}