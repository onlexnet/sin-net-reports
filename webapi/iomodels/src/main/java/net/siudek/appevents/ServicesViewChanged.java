package net.siudek.appevents;

import org.springframework.cloud.bus.event.RemoteApplicationEvent;

public class ServicesViewChanged extends RemoteApplicationEvent {

    /** Unique type id to check serialization. */
    private static final long serialVersionUID = 300971291522288790L;

}