package dev.ckateptb.webmorph.configuration.security.bearer;

import com.auth0.jwt.JWT;
import dev.ckateptb.webmorph.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WebFluxBearerConverter implements ServerAuthenticationConverter {
    private final AccountService accountService;

    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getCookies().getFirst("auth_token"))
                .map(httpCookie -> JWT.decode(httpCookie.getValue()))
                .flatMap(token -> this.accountService.findByUuid(UUID.fromString(token.getSubject()))
                        .map(account -> new BearerAuthenticationToken(account, token)));
    }
}
