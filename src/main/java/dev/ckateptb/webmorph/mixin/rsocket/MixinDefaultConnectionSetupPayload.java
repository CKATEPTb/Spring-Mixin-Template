package dev.ckateptb.webmorph.mixin.rsocket;

import dev.ckateptb.webmorph.configuration.rsocket.api.RSocketHolder;
import io.rsocket.RSocket;
import io.rsocket.core.DefaultConnectionSetupPayload;
import lombok.Getter;
import lombok.Setter;
import org.spongepowered.asm.mixin.Mixin;

// TODO JavaDoc
@Getter
@Setter
@Mixin(DefaultConnectionSetupPayload.class)
public class MixinDefaultConnectionSetupPayload implements RSocketHolder {
    private RSocket rSocket;
}
