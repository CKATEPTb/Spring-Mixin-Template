package dev.ckateptb.webmorph.account.exception;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException() {
        super("Invalid username or password.");
    }
}
