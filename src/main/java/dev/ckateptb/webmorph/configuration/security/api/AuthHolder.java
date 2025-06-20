package dev.ckateptb.webmorph.configuration.security.api;

import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

public interface AuthHolder {
    Mono<Authentication> getAuth();

    void setAuth(Supplier<Mono<Authentication>> auth);
}
