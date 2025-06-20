package dev.ckateptb.webmorph.configuration.security.bearer;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import dev.ckateptb.webmorph.account.AccountService;
import dev.ckateptb.webmorph.configuration.rsocket.api.RSocketHolder;
import dev.ckateptb.webmorph.configuration.security.api.AuthHolder;
import io.rsocket.metadata.WellKnownAuthType;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.security.core.Authentication;
import org.springframework.security.rsocket.api.PayloadExchange;
import org.springframework.security.rsocket.authentication.AuthenticationPayloadExchangeConverter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PayloadExchangeBearerConverter extends AuthenticationPayloadExchangeConverter {
    private final RSocketStrategies strategies;
    private final AccountService accountService;

    @Override
    public Mono<Authentication> convert(PayloadExchange exchange) {
        Map<String, Object> extract = this.strategies.metadataExtractor().extract(exchange.getPayload(), exchange.getMetadataMimeType());
        Object authType = extract.get("authType");
        AuthHolder authHolder = this.getAuthHolder(exchange);
        if (authType == WellKnownAuthType.SIMPLE) {
            String username = extract.get("username").toString().toLowerCase();
            String password = extract.get("password").toString();
            return this.simple(authHolder, username, password);
        } else if (authType == WellKnownAuthType.BEARER) {
            DecodedJWT decoded = JWT.decode(extract.get("token").toString());
            return this.bearer(authHolder, decoded);
        }
        return authHolder.getAuth();
    }

    private Mono<Authentication> simple(AuthHolder holder, String username, String password) {
        return this.accountService.generateToken(username, password, true)
                .map(JWT::decode)
                .flatMap(token -> this.bearer(holder, token));
    }

    private Mono<Authentication> bearer(AuthHolder holder, DecodedJWT token) {
        holder.setAuth(() -> this.accountService.findByUuid(UUID.fromString(token.getSubject()))
                .map(account -> new BearerAuthenticationToken(account, token)));
        return holder.getAuth();
    }

    private AuthHolder getAuthHolder(PayloadExchange exchange) {
        return (AuthHolder) ((RSocketHolder) exchange).getRSocket();
    }

}
