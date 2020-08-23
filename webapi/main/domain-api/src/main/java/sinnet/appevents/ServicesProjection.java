package sinnet.appevents;

import lombok.Data;

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
    final class Changed {
        private boolean ignored;
    }
}
