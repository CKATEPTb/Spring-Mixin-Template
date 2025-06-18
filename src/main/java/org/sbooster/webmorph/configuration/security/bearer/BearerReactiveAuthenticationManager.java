package org.sbooster.webmorph.configuration.security.bearer;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

// todo
@Slf4j
@Component
@RequiredArgsConstructor
public class BearerReactiveAuthenticationManager implements ReactiveAuthenticationManager {
//    @Value("org.sbooster.webmorph.jwt.secret")
//    private final String secret;
    private final Algorithm algorithm = Algorithm.HMAC256("this.secret");

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
//        if (authentication instanceof BearerAuthenticationToken bearerAuthentication) {
//            return this.validate(bearerAuthentication)
//                    .map(isValid -> {
//                        log.debug("Bearer token validation result for {} is {}", bearerAuthentication, isValid);
//                        bearerAuthentication.setAuthenticated(isValid);
//                        return bearerAuthentication;
//                    });
        /*} else*/ return Mono.error(new IllegalArgumentException("BearerPayloadExchangeConverter is only supported for now"));
    }

    private Mono<Boolean> validate(BearerAuthenticationToken authentication) {
//        UserDetails details = authentication.getPrincipal();
//        return Mono.fromCallable(() -> {
//            JWT.require(this.algorithm)
//                    .withSubject(String.valueOf(details.getId()))
//                    .withClaim("ema", userDetails.getUsername())
//                    .withClaim("pwd", userDetails.getPassword())
//                    .withArrayClaim("typ", userDetails.getAuthorities()
//                            .stream()
//                            .map(GrantedAuthority::getAuthority)
//                            .toArray(String[]::new)
//                    )
//                    .build().verify(token);
//            return true;
//        }).onErrorReturn(false);
        return Mono.just(true);
    }

}
