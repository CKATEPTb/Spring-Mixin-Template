package com.example.project;

import dev.ckateptb.webmorph.WebMorph;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Application {
    @SneakyThrows
    public static void main(String[] args) {
        WebMorph.bootstrap(args);
    }

}
