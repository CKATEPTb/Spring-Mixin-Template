package dev.ckateptb.webmorph.mixins.rsocket;

import io.rsocket.Payload;
import dev.ckateptb.webmorph.configuration.rsocket.interfaces.RSocketHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.springframework.security.rsocket.api.PayloadExchangeType;
import org.springframework.security.rsocket.core.DefaultPayloadExchange;
import org.springframework.util.MimeType;
import reactor.util.context.Context;

@Mixin(targets = "org.springframework.security.rsocket.core.PayloadInterceptorRSocket")
public class MixinPayloadInterceptorRSocket {
    @Shadow
    private Context context;

    @Redirect(method = "lambda$intercept$7", at = @At(
            value = "NEW",
            target = "Lorg/springframework/security/rsocket/core/DefaultPayloadExchange;<init>(Lorg/springframework/security/rsocket/api/PayloadExchangeType;Lio/rsocket/Payload;Lorg/springframework/util/MimeType;Lorg/springframework/util/MimeType;)V"
    ))
    private DefaultPayloadExchange extendDefaultPayloadExchange(PayloadExchangeType type, Payload payload, MimeType metadataMimeType,
                                                                MimeType dataMimeType) {
        DefaultPayloadExchange exchange = new DefaultPayloadExchange(type, payload, metadataMimeType, dataMimeType);
        ((RSocketHolder) exchange).setRSocket(this.context.get("rsocket"));
        return exchange;
    }
}
