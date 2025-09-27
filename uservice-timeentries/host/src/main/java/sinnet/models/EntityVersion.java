package sinnet.models;

/**
 * Represents the version of an entity.
 */
public sealed interface EntityVersion {

  /**
   * Indicates a new entity that has not been persisted yet.
   */
  enum New implements EntityVersion {
    INSTANCE
  }

  /**
   * Indicates an existing entity with a specific version value.
   */
  record Existing(long value) implements EntityVersion {

    /**
     * Constructor to validate the version value.
     */
    public Existing {
      // After migration from Spring Boot 3.x -> 3.4+ version==0 is not longer accepted in JPA operations when saving new entity.
      if (value <= 0) {
        throw new IllegalArgumentException("version must be positive");
      }
    }
  }

  /**
   * Converts an EntityVersion to its corresponding long value.
   *
   * @param version the EntityVersion instance
   * @return the long value representing the version
   */
  static long toDto(EntityVersion version) {
    return switch (version) {
      case New ignored -> 0L;
      case Existing e -> e.value();
    };
  }

  /**
   * Converts an EntityVersion to its corresponding long value.
   *
   * @param version the EntityVersion instance
   * @return the long value representing the version
   */
  static Long toDbo(EntityVersion version) {
    return switch (version) {
      case New ignored -> null;
      case Existing e -> e.value();
    };
  }

  /**
   * Creates an EntityVersion instance from a long value.
   *
   * @param version the long value representing the version
   * @return the corresponding EntityVersion instance
   */
  static EntityVersion of(long version) {
    return version == 0 ? New.INSTANCE : new Existing(version);
  }

}
