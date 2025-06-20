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
import org.springframework.security.rsocket.authentication.AuthenticationPayloadInterceptor;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;

/**
 * Core RSocket security configuration.
 * <p>
 * Enables reactive method security and configures authentication and metadata-based
 * authorization for RSocket payloads using a custom JWT-based flow.
 */
@Configuration
@EnableRSocketSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {
    /**
     * Configures the {@link AuthenticationPayloadInterceptor} for RSocket authentication.
     * <p>
     * Uses {@link BearerReactiveAuthenticationManager} to validate tokens and
     * {@link PayloadExchangeBearerConverter} to extract credentials from metadata.
     *
     * @param authenticationManager the reactive authentication manager
     * @param converter             the payload-to-authentication converter
     * @return the configured payload interceptor
     */
    @Bean
    public AuthenticationPayloadInterceptor payloadInterceptor(BearerReactiveAuthenticationManager authenticationManager, PayloadExchangeBearerConverter converter) {
        AuthenticationPayloadInterceptor authInterceptor = new AuthenticationPayloadInterceptor(authenticationManager);
        authInterceptor.setAuthenticationConverter(converter);
        authInterceptor.setOrder(PayloadInterceptorOrder.AUTHENTICATION.getOrder());
        return authInterceptor;
    }

    /**
     * Registers the RSocket security interceptor chain.
     * <p>
     * This example permits all payloads globally and assumes authorization is handled
     * at the method level via {@code @PreAuthorize}, {@code @PostAuthorize}, etc.
     *
     * @param rsocket     the RSocket security builder
     * @param interceptor the authentication payload interceptor
     * @return the final {@link PayloadSocketAcceptorInterceptor} for connection handling
     * todo научить обрабатывать hasPermission из luckperms
     */
    @Bean
    public PayloadSocketAcceptorInterceptor authorizationToken(RSocketSecurity rsocket, AuthenticationPayloadInterceptor interceptor) {
        return rsocket
                .addPayloadInterceptor(interceptor)
                .authorizePayload(authorize -> authorize.anyExchange().permitAll())
                .build();
    }

    /**
     * Removes the default "ROLE_" prefix from granted authorities.
     * <p>
     * Allows role-based logic to use authority names directly without Spring's
     * {@code ROLE_} prefix requirement.
     *
     * @return the {@link GrantedAuthorityDefaults} bean with empty prefix
     */
    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(Strings.EMPTY);
    }

    /**
     * Configures the default password encoder to use BCrypt.
     * <p>
     * This encoder is used to hash and validate passwords within the authentication flow.
     *
     * @return the configured {@link PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
