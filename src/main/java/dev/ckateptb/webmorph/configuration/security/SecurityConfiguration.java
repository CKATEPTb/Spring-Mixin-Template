package dev.ckateptb.webmorph.configuration.security;

import dev.ckateptb.webmorph.configuration.security.bearer.BearerReactiveAuthenticationManager;
import dev.ckateptb.webmorph.configuration.security.bearer.PayloadExchangeBearerConverter;
import net.luckperms.api.LuckPerms;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.PayloadInterceptorOrder;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.rsocket.authentication.AuthenticationPayloadExchangeConverter;
import org.springframework.security.rsocket.authentication.AuthenticationPayloadInterceptor;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;

import java.util.UUID;

@Configuration
@EnableRSocketSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {
    @Bean
    public AuthenticationPayloadInterceptor payloadInterceptor(BearerReactiveAuthenticationManager authenticationManager, PayloadExchangeBearerConverter converter) {
        AuthenticationPayloadInterceptor authInterceptor = new AuthenticationPayloadInterceptor(authenticationManager);
        authInterceptor.setAuthenticationConverter(converter);
        authInterceptor.setOrder(PayloadInterceptorOrder.AUTHENTICATION.getOrder());
        return authInterceptor;
    }

    @Bean
    public PayloadSocketAcceptorInterceptor authorizationToken(RSocketSecurity rsocket, AuthenticationPayloadInterceptor interceptor) {
        return rsocket
                .addPayloadInterceptor(interceptor)
                .authorizePayload(authorize -> authorize.anyExchange().permitAll())
                .build();
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(Strings.EMPTY);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
