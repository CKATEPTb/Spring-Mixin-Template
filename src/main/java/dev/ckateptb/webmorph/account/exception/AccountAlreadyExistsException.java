package dev.ckateptb.webmorph.account.exception;

public class AccountAlreadyExistsException extends RuntimeException {
    public AccountAlreadyExistsException() {
        super("Account with given username already exists.");
    }
}
