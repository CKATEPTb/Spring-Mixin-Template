package dev.ckateptb.webmorph.mixins.rsocket;

import io.rsocket.ConnectionSetupPayload;
import io.rsocket.RSocket;
import net.lenni0451.classtransform.InjectionCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import reactor.core.publisher.Mono;

@Mixin(targets = "org.springframework.security.rsocket.core.PayloadSocketAcceptor")
public class MixinPayloadSocketAcceptor {
    @Inject(method = "accept", at = @At(value = "RETURN"), cancellable = true)
    public void accept(ConnectionSetupPayload setup, RSocket sendingSocket, InjectionCallback callback) {
        Mono<RSocket> mono = callback.castReturnValue();
        callback.setReturnValue(mono != null ? mono.contextWrite(context -> context.put("rsocket", sendingSocket)) : Mono.empty());
    }
}
