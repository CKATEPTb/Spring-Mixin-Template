package dev.ckateptb.webmorph;

import dev.ckateptb.webmorph.account.AccountService;
import dev.ckateptb.webmorph.account.repository.AccountRepository;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.rsocket.metadata.AuthMetadataCodec;
import io.rsocket.metadata.WellKnownMimeType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.MimeType;

import java.net.URI;

@Slf4j
@SpringBootApplication
public class Application {
    @SneakyThrows
    public static void main(String[] args) {
        WebMorph.bootstrap(Application.class, args);
    }

}
