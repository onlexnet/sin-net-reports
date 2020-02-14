package sinnet.read;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sinnet.Name;

/**
 * Container class for related Query, Response and Application Notification
 * about 'RegisteredServices'.
 */
public abstract class RegisteredServices {

    /**
     * Query about data where returned projection should be already
     * limited to given date and serviceman.
     */
    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    public static final class Ask {
        /** FixMe. */
        private LocalDate filterToDay;
        /** FixMe. */
        private Name filterToServiceman;
    }

    /** Containsata produced as result of querying via {@link Query}. */
    @Data
    public static final class Reply {
        /** FixMe. */
        private ServiceEntry[] entries;
    }

    /** Data type used by {@link Reply}. */
    @NoArgsConstructor
    @Data
    public static final class ServiceEntry {
        /** FixMe. */
        private String who;
        /** FixMe. */
        private LocalDate when;
        /** FixMe. */
        private String whom;
        /** FixMe. */
        private String what;
        /** FixMe. */
        private int howLong;
        /** FixMe. */
        private int howFar;
    }
}
