package dev.ckateptb.webmorph.configuration.security.bearer;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.security.core.Authentication;
import org.springframework.security.rsocket.api.PayloadExchange;
import org.springframework.security.rsocket.authentication.AuthenticationPayloadExchangeConverter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

// todo
@Component
@RequiredArgsConstructor
public class PayloadExchangeBearerConverter extends AuthenticationPayloadExchangeConverter {
    private final RSocketStrategies strategies;

    @Override
    public Mono<Authentication> convert(PayloadExchange exchange) {
        Map<String, Object> extract = strategies.metadataExtractor().extract(exchange.getPayload(), exchange.getMetadataMimeType());
        if (!(extract.get("authentication") instanceof String token)) return Mono.empty();
        DecodedJWT decode = JWT.decode(token);
        return Mono.empty();
    }

}
