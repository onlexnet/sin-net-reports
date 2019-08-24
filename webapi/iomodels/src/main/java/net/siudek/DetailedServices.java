package net.siudek;

import java.time.LocalDate;

import lombok.Data;

/**
 * Query services provided for given customer and date.
 */
public abstract class DetailedServices {

    @Data
    public final class Query {
        private LocalDate when;
        private Name whom;
    }

    @Data
    public final class Reply {
        private ServiceEntry[] entries;
    }

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