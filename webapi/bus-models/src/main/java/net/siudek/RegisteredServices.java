package net.siudek;

import java.time.LocalDate;

import lombok.Data;

/**
 * Container class for related Query, Response and Application Notification
 * about 'RegisteredServices'.
 */
public abstract class RegisteredServices {

    /**
     * Query about data where returned projection should be already limited to given
     * date and serviceman.
     */
    @Data
    public final class Query {
        private LocalDate filterToDay;
        private Name filterToServiceman;
    }

    /** Containsata produced as result of querying via {@link Query} */
    @Data
    public final class Reply {

        private ServiceEntry[] entries;
    }

    /** Data type used by {@link Reply} */
    @Data
    public final class ServiceEntry {
        private String who;
        private LocalDate when;
        private String whom;
        private String what;
        private int howLong;
        private int howFar;
    }
}
