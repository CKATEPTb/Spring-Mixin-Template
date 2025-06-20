package dev.ckateptb.webmorph.account.controller;

import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

// TODO Должен авторизовывать сокет по metadataPush, а http по куки
@Controller
public class AuthenticateController {

    @ConnectMapping
    public Mono<Void> authenticate() {
        return Mono.empty();
    }
}
