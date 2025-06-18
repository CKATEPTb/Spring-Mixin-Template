package org.sbooster.webmorph.eventbus.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sbooster.webmorph.WebMorph;
import reactor.core.Disposable;
import reactor.util.function.Tuple2;

/**
 * Base class for all events used in the reactive EventBus system.
 *
 * <p>This class serves as the foundation for both cancellable and non-cancellable event types.
 * It supports dynamic dispatching and static registration of handlers based on event type.</p>
 *
 * <p>Each subclass of {@code Event} can be dispatched via {@link #dispatch()}, and can also
 * register listeners via static {@code on(...)} methods without requiring direct access to the {@link org.sbooster.webmorph.eventbus.EventBus}.</p>
 *
 * <p>Listeners are automatically associated with the calling event class using internal stack inspection.
 * This eliminates boilerplate code such as explicitly passing the event class for subscription.</p>
 *
 * @see CancelableEvent
 * @see EventHandler
 * @see EventPriority
 */
public abstract class Event {
    /**
     * Dispatches this event to all registered subscribers through the global {@link WebMorph#EVENT_BUS}.
     *
     * @param <T> the actual runtime type of this event
     * @return the same event instance after all listeners have been processed
     */
    @SuppressWarnings("unchecked")
    public <T extends Event> T dispatch() {
        return WebMorph.EVENT_BUS.dispatchEvent((T) this);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Event> Class<E> resolveCallerEventClass() {
        return (Class<E>) StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(frames -> frames
                        .filter(f -> {
                            String cls = f.getClassName();
                            return !cls.equals(Event.class.getName()) &&
                                    !cls.startsWith("org.sbooster.webmorph.eventbus");
                        })
                        .findFirst()
                        .orElseThrow()
                        .getDeclaringClass());
    }

    /**
     * Registers a handler for this event type with the specified priority and force execution setting.
     *
     * <p>If {@code force} is {@code true}, the handler will be invoked even if the event
     * has already been cancelled (only applies to {@link CancelableEvent}).</p>
     *
     * @param priority the priority level at which the handler should be invoked
     * @param force    whether to process cancelled events
     * @param handler  the event handler logic
     * @param <E>      the event type
     * @return a {@link Disposable} used to unregister the handler
     */
    public static <E extends Event> Disposable on(EventPriority priority, boolean force, EventHandler<E> handler) {
        return WebMorph.EVENT_BUS.on(resolveCallerEventClass(), priority, force, handler);
    }

    /**
     * Registers a handler with default priority ({@link EventPriority#NORMAL}) and the given force execution flag.
     *
     * @param force   whether to process cancelled events
     * @param handler the event handler logic
     * @param <E>     the event type
     * @return a {@link Disposable} used to unregister the handler
     */
    public static <E extends Event> Disposable on(boolean force, EventHandler<E> handler) {
        return on(EventPriority.NORMAL, force, handler);
    }

    /**
     * Registers a handler with the given priority. Cancelled events will be skipped.
     *
     * @param priority the handler's priority level
     * @param handler  the event handler logic
     * @param <E>      the event type
     * @return a {@link Disposable} used to unregister the handler
     */
    public static <E extends Event> Disposable on(EventPriority priority, EventHandler<E> handler) {
        return on(priority, false, handler);
    }

    /**
     * Registers a handler with default priority ({@link EventPriority#NORMAL}) and skips cancelled events.
     *
     * @param handler the event handler logic
     * @param <E>     the event type
     * @return a {@link Disposable} used to unregister the handler
     */
    public static <E extends Event> Disposable on(EventHandler<E> handler) {
        return on(EventPriority.NORMAL, false, handler);
    }
}
