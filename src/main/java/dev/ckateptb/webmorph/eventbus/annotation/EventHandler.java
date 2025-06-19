package dev.ckateptb.webmorph.eventbus.annotation;

import dev.ckateptb.webmorph.eventbus.EventBus;
import dev.ckateptb.webmorph.eventbus.event.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as an event handler that should be invoked when an event of a compatible type is published.
 *
 * <p>This annotation is used to register methods for reactive, non-blocking event handling via the EventBus system.
 * Methods annotated with {@code @EventHandler} must have exactly one parameter, representing the type of event
 * to handle. The method may return {@code void}.</p>
 *
 * <p>Handlers can define their execution order using the {@link #priority()} attribute. Lower priorities
 * execute earlier, while higher priorities are delayed. This allows fine-grained control over
 * the order in which listeners react to the same event.</p>
 *
 * <p>The {@link #force()} flag indicates whether this handler should be invoked even if the event has been
 * cancelled by a previous handler. This is useful for listeners that must observe or log cancelled events,
 * or for critical operations that must run regardless of cancellation state.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * @EventHandler(priority = EventPriority.HIGH, force = true)
 * public void onUserKicked(UserKickEvent event) {
 *     // Always handle, even if cancelled
 * }
 * }
 * </pre>
 *
 * @see EventBus
 * @see EventPriority
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {
    /**
     * Defines the execution priority of the event handler.
     * <p>Handlers with lower priority values are executed earlier.
     * This allows ordered control over event propagation.</p>
     *
     * @return the priority level of the handler
     */
    EventPriority priority() default EventPriority.NORMAL;

    /**
     * Determines whether the handler should be invoked even if the event
     * was cancelled by an earlier handler.
     * <p>Set this to {@code true} if the method should always run regardless
     * of cancellation status — useful for logging, monitoring, or fallback logic.</p>
     *
     * @return {@code true} to always invoke the handler; {@code false} to skip if cancelled
     */
    boolean force() default false;
}
