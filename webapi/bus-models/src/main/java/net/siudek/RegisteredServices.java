package net.siudek;

import java.time.LocalDate;

import lombok.Data;

/**
 * Container class for related Query, Response and Application Notification
 * about 'RegisteredServices'.
 */
public abstract class RegisteredServices {

    /**
     * Query about data where returned projection should be already
     * limited to given date and serviceman.
     */
    @Data
    public final class Query {
        /** FixMe. */
        private LocalDate filterToDay;
        /** FixMe. */
        private Name filterToServiceman;
    }

    /** Containsata produced as result of querying via {@link Query}. */
    @Data
    public final class Reply {
        /** FixMe. */
        private ServiceEntry[] entries;
    }

    /** Data type used by {@link Reply}. */
    @Data
    public final class ServiceEntry {
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
