package dev.ckateptb.webmorph.configuration.rsocket;

import dev.ckateptb.webmorph.configuration.rsocket.exception.ExceptionMessageHandlerAdvice;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.rsocket.metadata.AuthMetadataCodec;
import io.rsocket.metadata.WellKnownAuthType;
import io.rsocket.metadata.WellKnownMimeType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver;
import org.springframework.util.MimeType;
import org.springframework.validation.Validator;
import org.springframework.web.method.ControllerAdviceBean;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;

// todo
@Configuration
public class RSocketConfiguration {
    private static final MimeType MESSAGE_RSOCKET_AUTHENTICATION = MimeType.valueOf(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString());

    @Bean
    public RSocketStrategies getRSocketStrategies() {
        return RSocketStrategies.builder()
                .encoder(new Jackson2JsonEncoder())
                .decoder(new Jackson2JsonDecoder())
                .routeMatcher(new PathPatternRouteMatcher())
                .metadataExtractorRegistry(registry -> {
                    registry.metadataToExtract(MESSAGE_RSOCKET_AUTHENTICATION, byte[].class, (s, stringObjectMap) -> {
                        ByteBuf rawAuthentication = Unpooled.wrappedBuffer(s);
                        WellKnownAuthType value = AuthMetadataCodec.readWellKnownAuthType(rawAuthentication);
                        switch (value) {
                            case BEARER ->
                                    stringObjectMap.put("authentication", new String(AuthMetadataCodec.readBearerTokenAsCharArray(rawAuthentication)));
                            case SIMPLE ->
                                    throw new UnsupportedOperationException("Simple authentication not supported!");
                        }
                    });
                })
                .build();
    }

    @Bean
    public RSocketMessageHandler messageHandler(RSocketStrategies strategies, Validator validator, ApplicationContext context) {
        RSocketMessageHandler messageHandler = new RSocketMessageHandler();
        messageHandler.getArgumentResolverConfigurer().addCustomResolver(new AuthenticationPrincipalArgumentResolver());
        messageHandler.setRSocketStrategies(strategies);
        messageHandler.setValidator(validator);
        ControllerAdviceBean.findAnnotatedBeans(context).forEach(bean ->
                messageHandler.registerMessagingAdvice(new ExceptionMessageHandlerAdvice(bean)));
        return messageHandler;
    }
}
