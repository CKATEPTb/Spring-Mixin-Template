package com.example.project.components;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExampleComponent {
    @SneakyThrows
    @PostConstruct
    public void init() {
        log.info("Hello World!");
    }
}
