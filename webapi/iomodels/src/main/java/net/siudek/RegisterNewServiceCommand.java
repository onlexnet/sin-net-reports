package net.siudek;

import java.time.Duration;
import java.time.LocalDate;

import lombok.Value;
import lombok.With;

@Value
public class RegisterNewServiceCommand {
    @With private String serviceActionId;
    private Name who;
    private LocalDate when;
    private Name whom;
    private String what;
    private Duration howLong;
    private Distance howFar;
}