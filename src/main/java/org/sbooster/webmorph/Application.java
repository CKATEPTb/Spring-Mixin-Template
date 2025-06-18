package org.sbooster.webmorph;

import org.sbooster.webmorph.events.MixinTransformerRegistrationEvent;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        // Если собираетесь использовать миксины, то разеристрируйте пакет с их содержимым перед бутстрапом
        MixinTransformerRegistrationEvent.<MixinTransformerRegistrationEvent>on(event ->
                event.addTransformer("org.example.package.mixins.**"));
        WebMorph.bootstrap(args); // WebMorph сам инициализирует mixin'ы и spring context
    }
}
