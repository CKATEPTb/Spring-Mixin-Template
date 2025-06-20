package dev.ckateptb.webmorph.configuration.security.bearer;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import dev.ckateptb.webmorph.account.model.Account;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class BearerReactiveAuthenticationManager implements ReactiveAuthenticationManager {
    private final Optional<Algorithm> algorithm;
    @Getter
    private final Algorithm dummyAlgorithm = Algorithm.HMAC256("dummy");

    @PostConstruct
    public void warnOnDummyAlgorithm() {
        if (this.algorithm.isEmpty()) log.warn("""
                No authentication algorithm configured. Algorithm bean not found in current application context.""");
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return this.validate(authentication)
                .doOnNext(authentication::setAuthenticated)
                .thenReturn(authentication);
    }

    public Mono<Boolean> validate(Authentication authentication) {
        if (!(authentication instanceof BearerAuthenticationToken token)) return Mono.just(false);
        Account principal = token.getPrincipal();
        return Mono.fromCallable(() -> {
            JWT.require(this.algorithm.orElse(this.dummyAlgorithm))
                    .withSubject(principal.getUuid().toString())
                    .withClaim("ema", principal.getUsername())
                    .withClaim("pwd", principal.getMetadata("password"))
                    .build()
                    .verify(token.getCredentials());
            return true;
        }).onErrorReturn(false);
    }

    public Mono<String> generateJWT(Account account, boolean rememberMe) {
        Instant now = Instant.now();
        return Mono.fromCallable(() -> JWT.create()
                .withSubject(account.getUuid().toString())
                .withClaim("ema", account.getUsername())
                .withClaim("pwd", account.getMetadata("password"))
                .withExpiresAt(rememberMe ? now.plus(12, ChronoUnit.HOURS) : now.plus(6, ChronoUnit.HOURS)) // TODO: Сделать ивент JWTSignEvent и там уже вешать expires
                .sign(this.algorithm.orElse(this.dummyAlgorithm)));
    }
}
