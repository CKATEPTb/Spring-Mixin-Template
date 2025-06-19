package dev.ckateptb.webmorph.mixin.rsocket;

import dev.ckateptb.webmorph.configuration.rsocket.api.RSocketHolder;
import io.rsocket.RSocket;
import lombok.Getter;
import lombok.Setter;
import org.spongepowered.asm.mixin.Mixin;
import org.springframework.security.rsocket.core.DefaultPayloadExchange;

@Getter
@Setter
@Mixin(DefaultPayloadExchange.class)
public class MixinDefaultPayloadExchange implements RSocketHolder {
    private RSocket rSocket;
}
