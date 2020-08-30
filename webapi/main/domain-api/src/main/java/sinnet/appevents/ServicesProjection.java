package sinnet.appevents;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Container class for related Query, Response and Application Notification
 * about 'ServicesProjection'.
 */
public interface ServicesProjection {

    /**
     * Emited when ServiceProjection data have been changed
     * (add / update / delete).
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    final class Changed {
        private UUID correlactionId;
    }
}
