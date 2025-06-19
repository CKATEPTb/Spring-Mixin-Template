package dev.ckateptb.webmorph.mixins.rsocket;

import org.spongepowered.asm.mixin.Mixin;
import org.springframework.security.rsocket.authentication.AuthenticationPayloadExchangeConverter;

@Mixin(value = AuthenticationPayloadExchangeConverter.class)
public class MixinAuthenticationPayloadExchangeConverter {
//    @Inject(method = "convert", at = @At(value = "RETURN"), cancellable = true)
//    public void convert(PayloadExchange exchange, InjectionCallback callback) {
//        Mono<Authentication> mono = callback.castReturnValue();
//        if (exchange instanceof RSocketHolder holder && holder.getRSocket() instanceof AuthHolder authHolder) {
//            System.out.println(authHolder.getAuth());
//            callback.setReturnValue(mono.doOnNext(authHolder::setAuth).switchIfEmpty(Mono.justOrEmpty(authHolder.getAuth())));
//        } else callback.setReturnValue(mono);
//    }
//
//    @Overwrite
//    private Authentication simple(ByteBuf rawAuthentication) {
//        ByteBuf rawUsername = AuthMetadataCodec.readUsername(rawAuthentication).toString(StandardCharsets.UTF_8);
//        String username = rawUsername.toString(StandardCharsets.UTF_8);
//        ByteBuf rawPassword = AuthMetadataCodec.readPassword(rawAuthentication);
//        String password = rawPassword.toString(StandardCharsets.UTF_8);
//        return UsernamePasswordAuthenticationToken.unauthenticated(username, password);
//    }
//
//    private Authentication bearer(ByteBuf rawAuthentication) {
//        char[] rawToken = AuthMetadataCodec.readBearerTokenAsCharArray(rawAuthentication);
//        String token = new String(rawToken);
//        return new BearerTokenAuthenticationToken(token);
//    }
}
