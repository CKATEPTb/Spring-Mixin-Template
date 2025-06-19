package dev.ckateptb.webmorph.mixins.rsocket;

import io.rsocket.RSocket;
import lombok.Getter;
import lombok.Setter;
import dev.ckateptb.webmorph.configuration.rsocket.interfaces.RSocketHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.springframework.security.rsocket.core.DefaultPayloadExchange;

@Getter
@Setter
@Mixin(DefaultPayloadExchange.class)
public class MixinDefaultPayloadExchange implements RSocketHolder {
    private RSocket rSocket;
}
