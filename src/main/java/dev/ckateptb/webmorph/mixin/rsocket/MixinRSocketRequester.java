package dev.ckateptb.webmorph.mixin.rsocket;

import dev.ckateptb.webmorph.configuration.security.api.AuthHolder;
import lombok.Getter;
import lombok.Setter;
import org.spongepowered.asm.mixin.Mixin;
import org.springframework.security.core.Authentication;

@Getter
@Setter
@Mixin(targets = "io.rsocket.core.RSocketRequester")
public class MixinRSocketRequester implements AuthHolder {
    private Authentication auth;
}
