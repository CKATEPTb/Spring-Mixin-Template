package dev.ckateptb.webmorph;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lenni0451.classtransform.TransformerManager;
import net.lenni0451.classtransform.additionalclassprovider.GuavaClassPathProvider;
import net.lenni0451.classtransform.mixinstranslator.MixinsTranslator;
import net.lenni0451.classtransform.utils.loader.EnumLoaderPriority;
import net.lenni0451.classtransform.utils.loader.InjectionClassLoader;
import net.lenni0451.reflect.Agents;
import net.lenni0451.reflect.ClassLoaders;
import net.lenni0451.reflect.Methods;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Set;

@Slf4j
@SpringBootApplication
public class Application {
    private static Instrumentation instrumentation;

    public static void agentmain(final String args, final Instrumentation instrumentation) {
        Application.instrumentation = instrumentation;
    }

    public static void premain(final String args, final Instrumentation instrumentation) {
        Application.instrumentation = instrumentation;
    }

    public static void main(String[] args) throws Throwable {
        Class<Application> clazz = Application.class;
        TransformerManager transformer = new TransformerManager(new GuavaClassPathProvider());
        transformer.addTransformerPreprocessor(new MixinsTranslator());
        transformer.addTransformer(clazz.getPackageName() + ".mixins.**");
        BootstrapMode mode = BootstrapMode.NATIVE;
        Runnable bootstrap = () -> Application.bootstrap(clazz, GuavaClassPathProvider.class.getClassLoader(), args);
        if (instrumentation != null) {
            transformer.hookInstrumentation(instrumentation);
            mode = BootstrapMode.AGENT;
        } else {
            try {
                transformer.hookInstrumentation(Agents.getInstrumentation());
                mode = BootstrapMode.DUMMY;
            } catch (Throwable throwable) {
                URL[] path = ClassLoaders.getSystemClassPath();
                InjectionClassLoader injectionClassLoader = new InjectionClassLoader(transformer, path);
                injectionClassLoader.setPriority(EnumLoaderPriority.PARENT_FIRST);
                Thread.currentThread().setContextClassLoader(injectionClassLoader);
                Class<?> aClass = injectionClassLoader.loadClass(clazz.getName());
                bootstrap = () -> {
                    Method method = Methods.getDeclaredMethod(aClass, "bootstrap", Class.class, ClassLoader.class, String[].class);
                    if (method == null) throw new RuntimeException("bootstrap method not found");
                    Methods.invoke(null, method, aClass, injectionClassLoader, args);
                };
                mode = BootstrapMode.INJECT;
            }
        }
        bootstrap.run();
        log.info("Bootstrap complete. Mode: {} - {}", mode, mode.description);
        Set<String> transformers = transformer.getRegisteredTransformer();
        log.info("Registered mixin's ({}): {}", transformers.size(), transformers);
    }

    public static void bootstrap(Class<?> clazz, ClassLoader classLoader, String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(clazz)
                .headless(true)
                .bannerMode(Banner.Mode.OFF)
                .initializers(ctx -> ctx.setClassLoader(classLoader))
                .run(args);
    }

    @RequiredArgsConstructor
    private enum BootstrapMode {
        NATIVE("The application is running in native mode."),
        AGENT("The application is running using itself as an agent."),
        DUMMY("The application is running without an agent, a dummy has been substituted."),
        INJECT("The application is run without an agent, in class loader injection mode.");

        public final String description;
    }
}
