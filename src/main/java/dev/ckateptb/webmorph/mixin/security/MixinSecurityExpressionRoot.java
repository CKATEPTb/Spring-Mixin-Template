package dev.ckateptb.webmorph.mixin.security;

import dev.ckateptb.webmorph.account.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;

import java.util.function.Supplier;

@Slf4j
@Mixin(SecurityExpressionRoot.class)
public abstract class MixinSecurityExpressionRoot extends SecurityExpressionRoot {
    public MixinSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    public MixinSecurityExpressionRoot(Supplier<Authentication> authentication) {
        super(authentication);
    }

    @Shadow
    public abstract Object getPrincipal();

    @Overwrite
    public boolean hasPermission(Object target, Object permission) {
        return this.hasPermission((String) permission);
    }

    @Overwrite
    public boolean hasPermission(Object targetId, String targetType, Object permission) {
        return this.hasPermission((String) permission);
    }

    public boolean hasPermission(String permission) {
        return ((Account) this.getPrincipal()).hasPermission(permission);
    }
}
