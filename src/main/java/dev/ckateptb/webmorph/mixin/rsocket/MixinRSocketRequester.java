package dev.ckateptb.webmorph.mixin.rsocket;

import dev.ckateptb.webmorph.configuration.security.api.AuthHolder;
import lombok.Setter;
import org.spongepowered.asm.mixin.Mixin;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

@Setter
@Mixin(targets = "io.rsocket.core.RSocketRequester")
public class MixinRSocketRequester implements AuthHolder {
    private Supplier<Mono<Authentication>> auth = Mono::empty;

    @Override
    public Mono<Authentication> getAuth() {
        return this.auth.get();
    }
}
