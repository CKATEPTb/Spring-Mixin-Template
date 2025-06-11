package com.example.name;

import lombok.extern.slf4j.Slf4j;
import net.lenni0451.classtransform.TransformerManager;
import net.lenni0451.classtransform.additionalclassprovider.GuavaClassPathProvider;
import net.lenni0451.classtransform.mixinstranslator.MixinsTranslator;
import net.lenni0451.reflect.Agents;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

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
                .initializers(ctx -> ctx.setClassLoader(classLoader))
                .run(args);
        Set<String> transformers = transformer.getRegisteredTransformer();
        log.info("Registered mixin's ({}): {}", transformers.size(), transformers);
    }
}
