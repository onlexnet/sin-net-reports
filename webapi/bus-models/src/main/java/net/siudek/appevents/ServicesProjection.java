package net.siudek.appevents;

/**
 * Container class for related Query, Response and Application Notification
 * about 'ServicesProjection'.
 */
public abstract class ServicesProjection {

    /** Emited when ServiceProject has changed. */
    public final class Changed {
        /** Unique type id to check serialization. */
        private static final long serialVersionUID = 300971291522288790L;
    }
}
