package sinnet.read;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;

/** Container class for 'DailyReport' Query. */
public abstract class DailyReports {

    /** Ask part of Query. */
    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    public static class Ask {
        /** Date when services has been done. */
        private LocalDate when;
    }

    /** Marker type for Replies. */
    public abstract static class Reply {

        /** Private ctor to prevent creation of marker class. */
        private Reply() {
        }

        /** Answer is: no data. */
        public static class None extends Reply {

        }

        /** Answer is: Some data exists. */
        @NoArgsConstructor
        @Data
        @EqualsAndHashCode(callSuper = false)
        @AllArgsConstructor
        @Builder
        public static class Some extends Reply {
            /** What to add ... data. */
            @Singular
            private List<ServiceSummary> entries;
        }
    }

    /** Description of a single service. */
    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class ServiceSummary {
        /** Date when services has been done. */
        private LocalDate when;
    }
}
