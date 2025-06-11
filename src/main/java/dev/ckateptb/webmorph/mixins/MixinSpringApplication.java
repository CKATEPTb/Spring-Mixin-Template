package dev.ckateptb.webmorph.mixins;

import lombok.extern.slf4j.Slf4j;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.springframework.boot.SpringApplication;

@Slf4j
@Mixin(value = SpringApplication.class)
public class MixinSpringApplication {
    @Inject(method = "run([Ljava/lang/String;)Lorg/springframework/context/ConfigurableApplicationContext;", at = @At(value = "RETURN"))
    public void run(String... args) {
        log.info("Hello Mixin!");
    }
}
