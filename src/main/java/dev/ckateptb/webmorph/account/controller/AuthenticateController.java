package dev.ckateptb.webmorph.account.controller;

import dev.ckateptb.webmorph.account.AccountService;
import io.netty.buffer.ByteBufAllocator;
import io.rsocket.metadata.AuthMetadataCodec;
import io.rsocket.metadata.WellKnownMimeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticateController {
    private final AccountService accountService;

    public Mono<AuthenticateResponse> auth(AuthenticateRequest request) {
        return this.accountService.generateToken(request.username, request.password, request.rememberMe)
                .map(AuthenticateResponse::new);
    }

    @PreAuthorize("isAnonymous()")
    @MessageMapping("account.auth")
    public Mono<AuthenticateResponse> auth(RSocketRequester requester, @Payload AuthenticateRequest request) {
        return this.auth(request).flatMap(response -> requester
                .metadata(AuthMetadataCodec.encodeBearerMetadata(
                        ByteBufAllocator.DEFAULT,
                        response.token.toCharArray()
                ), MimeType.valueOf(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString()))
                .sendMetadata().thenReturn(response));
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/account/auth")
    public Mono<ResponseEntity<AuthenticateResponse>> restAuth(@RequestBody AuthenticateRequest request) {
        return this.auth(request).map(response -> {
            ResponseCookie cookie = ResponseCookie.from("auth_token", response.token())
                    .httpOnly(false)
                    .secure(true)
                    .path("/")
                    .maxAge(request.rememberMe ? Duration.ofDays(365) : Duration.ofHours(6)) // todo вынести в ивент
                    .build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(response);
        });
    }

    public record AuthenticateResponse(String token) {

    }

    public record AuthenticateRequest(String username, String password, boolean rememberMe) {

    }
}
