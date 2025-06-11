package com.example.name;

import lombok.extern.slf4j.Slf4j;
import net.lenni0451.classtransform.TransformerManager;
import net.lenni0451.classtransform.additionalclassprovider.GuavaClassPathProvider;
import net.lenni0451.classtransform.mixinstranslator.MixinsTranslator;
import net.lenni0451.reflect.Agents;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;
import java.util.Set;

@Slf4j
@SpringBootApplication
public class Application {
    public static void main(String[] args) throws Throwable {
        Class<Application> clazz = Application.class;
        TransformerManager transformer = new TransformerManager(new GuavaClassPathProvider());
        transformer.addTransformerPreprocessor(new MixinsTranslator());
        transformer.addTransformer(clazz.getPackageName() + ".mixins.**");
        transformer.hookInstrumentation(Agents.getInstrumentation());
        ClassLoader classLoader = GuavaClassPathProvider.class.getClassLoader();
        new SpringApplicationBuilder(clazz)
                .headless(true)
                .bannerMode(Banner.Mode.OFF)
                .initializers(ctx -> {
                    ctx.setClassLoader(classLoader);
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
    }
}
