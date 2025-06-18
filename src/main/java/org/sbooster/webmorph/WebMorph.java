package org.sbooster.webmorph;

import dev.ckateptb.reflection.Reflect;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.lenni0451.classtransform.TransformerManager;
import net.lenni0451.classtransform.additionalclassprovider.GuavaClassPathProvider;
import net.lenni0451.classtransform.mixinstranslator.MixinsTranslator;
import net.lenni0451.reflect.Agents;
import org.sbooster.webmorph.configuration.WebMorphConfiguration;
import org.sbooster.webmorph.eventbus.EventBus;
import org.sbooster.webmorph.events.MixinTransformerRegistrationEvent;
import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

@Slf4j
public class WebMorph {
    public static final EventBus EVENT_BUS = new EventBus();

    @SneakyThrows
    public static AnnotationConfigApplicationContext bootstrap(String[] args) {
        Class<?> clazz = getCallerClass();
        TransformerManager transformer = new TransformerManager(new GuavaClassPathProvider());
        transformer.addTransformerPreprocessor(new MixinsTranslator());
        MixinTransformerRegistrationEvent event = new MixinTransformerRegistrationEvent();
        event.addTransformer(WebMorph.class.getPackageName() + ".mixins.**");
        event.dispatch();
        event.getTransformers().forEach(transformer::addTransformer);
        transformer.hookInstrumentation(Agents.getInstrumentation());
        ClassLoader classLoader = GuavaClassPathProvider.class.getClassLoader();
        AnnotationConfigApplicationContext context = (AnnotationConfigApplicationContext) new SpringApplicationBuilder(clazz)
                .sources(WebMorphConfiguration.class, clazz)
                .headless(true)
                .bannerMode(Banner.Mode.OFF)
                .initializers(ctx -> {
                    ctx.setClassLoader(classLoader);
                    ctx.getBeanFactory().registerSingleton(EventBus.class.getName(), EVENT_BUS);
                    ConfigurableEnvironment environment = ctx.getEnvironment();
                    try (InputStream input = classLoader.getResourceAsStream("application.properties")) {
                        if (input != null) {
                            Properties props = new Properties();
                            props.load(input);
                            PropertiesPropertySource source = new PropertiesPropertySource("properties", props);
                            environment.getPropertySources().addLast(source);
                        }
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .run(args);
        Set<String> transformers = transformer.getRegisteredTransformer();
        log.info("Registered mixin's ({}): {}", transformers.size(), transformers);
        return context;
    }

    /**
     * Attempts to detect the main application class that called WebMorph.bootstrap().
     */
    private static Class<?> getCallerClass() {
        return Arrays.stream(Thread.currentThread().getStackTrace())
                .map(StackTraceElement::getClassName)
                .filter(name -> !name.startsWith("org.sbooster.webmorph"))
                .map(clazz -> {
                    try {
                        return Reflect.on(clazz);
                    } catch (Throwable e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(clazz -> {
                    try {
                        return clazz.method("main", String[].class).isStatic();
                    } catch (Throwable e) {
                        return false;
                    }
                })
                .map(Reflect::raw)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not detect caller main class"));
    }
}
