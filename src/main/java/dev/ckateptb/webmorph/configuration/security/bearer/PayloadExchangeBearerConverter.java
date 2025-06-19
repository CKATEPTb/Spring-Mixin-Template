package dev.ckateptb.webmorph.configuration.security.bearer;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import dev.ckateptb.webmorph.configuration.rsocket.api.RSocketHolder;
import dev.ckateptb.webmorph.configuration.security.api.AuthHolder;
import lombok.RequiredArgsConstructor;
import net.luckperms.api.LuckPerms;
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
    private final LuckPerms luckPerms;

    @Override
    public Mono<Authentication> convert(PayloadExchange exchange) {
        Map<String, Object> extract = this.strategies.metadataExtractor().extract(exchange.getPayload(), exchange.getMetadataMimeType());
        if (!(extract.get("auth") instanceof String token)) return Mono.justOrEmpty(this.getAuthHolder(exchange).getAuth());
        DecodedJWT decoded = JWT.decode(token);
        UUID accountId = UUID.fromString(decoded.getSubject());
        return Mono.fromFuture(this.luckPerms.getUserManager().loadUser(accountId))
                .map(user -> new BearerAuthenticationToken(user, decoded))
                .doOnNext(auth -> this.getAuthHolder(exchange).setAuth(auth))
                .cast(Authentication.class);
    }

    private AuthHolder getAuthHolder(PayloadExchange exchange) {
        return (AuthHolder) ((RSocketHolder) exchange).getRSocket();
    }

}
