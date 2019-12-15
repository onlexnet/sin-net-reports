package net.siudek;

import java.time.Duration;
import java.time.LocalDate;

import lombok.Value;
import lombok.With;

@Value
public class RegisterNewServiceCommand {

    /** Unique ID of service action. */
    @With
    private String serviceActionId;

    /** Serviceman who did the service. */
    private Name who;

    /** Date when the service has been provided. */
    private LocalDate when;

    /** Name of the Customer whom the service has been provided. */
    private Name whom;

    /** Descriptive info what actually has been provided as the service. */
    private String what;

    /** How much time take overall activity related to provide the service. */
    private Duration howLong;

    /** How far is distance to the place where service has been provided. */
    private Distance howFar;
}