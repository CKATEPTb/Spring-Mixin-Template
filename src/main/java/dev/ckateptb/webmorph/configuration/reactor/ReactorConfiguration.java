package dev.ckateptb.webmorph.configuration.reactor;

import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Hooks;

import java.util.HashSet;
import java.util.Set;

/**
 * Spring configuration that customizes Reactor's global error-handling behavior.
 *
 * <p>This configuration suppresses logging for specific types of exceptions that are considered
 * expected or non-critical in the application's reactive pipelines. Examples include
 * {@link org.springframework.security.access.AccessDeniedException} or
 * {@link io.netty.handler.codec.http.websocketx.WebSocketServerHandshakeException},
 * which often represent client-driven control flows rather than server-side failures.</p>
 *
 * <p>By default, Reactor logs all unhandled errors via {@code onErrorDropped}, which can lead
 * to excessive or noisy logging. This class installs a global {@code onErrorDropped} hook that
 * filters out errors of muted types, while still logging unexpected exceptions.</p>
 *
 * <p>Additional exception types can be muted or unmuted at runtime via the {@link #mute(Class)}
 * and {@link #unmute(Class)} methods.</p>
 *
 * <p>This configuration is applied eagerly during bean construction.</p>
 */
@Slf4j
@Configuration
public class ReactorConfiguration {
    private final Set<Class<? extends Throwable>> muted = new HashSet<>();

    public ReactorConfiguration() {
        this.mute(AccessDeniedException.class);
        this.mute(WebSocketServerHandshakeException.class);
        Hooks.resetOnErrorDropped();
        Hooks.onErrorDropped(throwable -> {
            if (this.muted.stream().noneMatch(t -> t.isAssignableFrom(throwable.getClass()))) {
                log.error("Error occurred", throwable);
            }
        });
    }

    /**
     * Adds an exception type to the muted list, suppressing it from global onErrorDropped logs.
     *
     * @param throwable the exception class to suppress
     */
    public synchronized void mute(Class<? extends Throwable> throwable) {
        this.muted.add(throwable);
    }

    /**
     * Removes an exception type from the muted list, allowing it to be logged again
     * if it occurs during onErrorDropped handling.
     *
     * @param throwable the exception class to unmute
     */
    public synchronized void unmute(Class<? extends Throwable> throwable) {
        this.muted.remove(throwable);
    }
}
