package dev.ckateptb.webmorph.mixin.rsocket;

import io.rsocket.ConnectionSetupPayload;
import io.rsocket.RSocket;
import net.lenni0451.classtransform.InjectionCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import reactor.core.publisher.Mono;

/**
 * Injects the sending {@link RSocket} into the Reactor {@link reactor.util.context.Context}
 * after the {@code PayloadSocketAcceptor#accept} method returns.
 * <p>
 * This makes the sending socket available downstream in the reactive context,
 * allowing other components (like interceptors) to access it via context lookup.
 * <p>
 * Part.1 of closing FIX-ME in Spring Security RSocket: "do we want to make the sendingSocket available in the PayloadExchange".
 */
@Mixin(targets = "org.springframework.security.rsocket.core.PayloadSocketAcceptor")
public class MixinPayloadSocketAcceptor {
    @Inject(method = "accept", at = @At(value = "RETURN"), cancellable = true)
    public void accept(ConnectionSetupPayload setup, RSocket sendingSocket, InjectionCallback callback) {
        Mono<RSocket> mono = callback.castReturnValue();
        callback.setReturnValue(mono != null ? mono.contextWrite(context -> context.put("rsocket", sendingSocket)) : Mono.empty());
    }
}
