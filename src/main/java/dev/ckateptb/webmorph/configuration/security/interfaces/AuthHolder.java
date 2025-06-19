package dev.ckateptb.webmorph.configuration.security.interfaces;

import org.springframework.security.core.Authentication;

public interface AuthHolder {
    Authentication getAuth();

    void setAuth(Authentication auth);
}
