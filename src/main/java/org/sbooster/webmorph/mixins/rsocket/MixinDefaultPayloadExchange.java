package org.sbooster.webmorph.mixins.rsocket;

import io.rsocket.RSocket;
import lombok.Getter;
import lombok.Setter;
import org.sbooster.webmorph.configuration.rsocket.interfaces.RSocketHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.springframework.security.rsocket.core.DefaultPayloadExchange;

@Getter
@Setter
@Mixin(DefaultPayloadExchange.class)
public class MixinDefaultPayloadExchange implements RSocketHolder {
    private RSocket rSocket;
}
