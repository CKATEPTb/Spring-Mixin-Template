package org.sbooster.webmorph.configuration.security;

import org.apache.logging.log4j.util.Strings;
import org.opencv.core.Algorithm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.PayloadInterceptorOrder;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.rsocket.authentication.AuthenticationPayloadExchangeConverter;
import org.springframework.security.rsocket.authentication.AuthenticationPayloadInterceptor;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;
import reactor.core.publisher.Mono;

//todo
@Configuration
@EnableRSocketSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {
    @Bean
    public PayloadSocketAcceptorInterceptor authorizationToken(RSocketSecurity rsocket) {
        return rsocket
//                .addPayloadInterceptor((exchange, chain) -> {
//                    AuthenticationPayloadInterceptor result = new AuthenticationPayloadInterceptor();
//                    result.setAuthenticationConverter(new AuthenticationPayloadExchangeConverter());
//                    result.setOrder(PayloadInterceptorOrder.AUTHENTICATION.getOrder());
//                    return result.intercept(exchange, chain);
//                })
                .authorizePayload(authorize -> authorize.anyExchange().permitAll())
                .build();
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(Strings.EMPTY);
    }
}
