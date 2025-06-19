package dev.ckateptb.webmorph.account.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class AuthenticateController {
    @PostMapping("account/authenticate")
    @MessageMapping("account.authenticate")
    public Mono<Void> authenticate(@Validated @RequestBody @Payload AuthenticateRequest request) {

    }

    public record AuthenticateRequest(
            String username,
            String password,
            boolean remember
    ) {
    }
}
