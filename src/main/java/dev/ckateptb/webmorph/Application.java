package dev.ckateptb.webmorph;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Application {
    @SneakyThrows
    public static void main(String[] args) {
        WebMorph.bootstrap(Application.class, args);
    }

}
