package dev.ckateptb.webmorph.mixins.rsocket;

import lombok.Getter;
import lombok.Setter;
import dev.ckateptb.webmorph.configuration.security.interfaces.AuthHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.springframework.security.core.Authentication;

@Getter
@Setter
@Mixin(targets = "io.rsocket.core.RSocketRequester")
public class MixinRSocketRequester implements AuthHolder {
    private Authentication auth;
}
