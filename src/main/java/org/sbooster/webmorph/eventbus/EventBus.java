package org.sbooster.webmorph.eventbus;

import dev.ckateptb.reflection.Reflect;
import lombok.extern.slf4j.Slf4j;
import org.sbooster.webmorph.eventbus.event.CancelableEvent;
import org.sbooster.webmorph.eventbus.event.Event;
import org.sbooster.webmorph.eventbus.event.EventHandler;
import org.sbooster.webmorph.eventbus.event.EventPriority;
import org.sbooster.webmorph.eventbus.listener.Listener;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * Reactive, priority-based event bus built on top of Reactor Core.
 *
 * <p>This class provides a publish-subscribe mechanism for dispatching and handling
 * asynchronous events in a non-blocking, backpressure-aware way. Events are published
 * to an internal {@link reactor.core.publisher.Sinks.Many} and dispatched to all
 * subscribed handlers based on their {@link org.sbooster.webmorph.eventbus.event.EventPriority}.</p>
 *
 * <p>Supports both manually registered handlers via {@link #on(Class, EventPriority, boolean, EventHandler)}
 * and declarative registration via {@link org.sbooster.webmorph.eventbus.annotation.EventHandler}
 * annotated methods in {@link org.sbooster.webmorph.eventbus.listener.Listener} components.</p>
 *
 * <p>Cancelable events are skipped for non-forced handlers once cancelled.</p>
 */
@Slf4j
public class EventBus {
    /**
     * Sink used to publish incoming events to subscribers.
     * Events are emitted as {@code (priority, event)} tuples to support ordered handling.
     */
    private final Sinks.Many<Tuple2<EventPriority, Event>> events = Sinks.many().multicast().onBackpressureBuffer();
    /**
     * Shared Flux stream for all event emissions, consumed by handlers.
     */
    private final Flux<Tuple2<EventPriority, Event>> handlers = this.events.asFlux();

    /**
     * Dispatches the given event to all handlers in order of {@link EventPriority}.
     *
     * <p>Each priority level receives a separate emission of the same event,
     * allowing handlers at different levels to react in isolation. Cancelable events
     * may prevent further propagation depending on handler configuration.</p>
     *
     * @param event the event instance to dispatch
     * @param <E>   the type of the event
     * @return the same event instance, for chaining or inspection
     */
    public <E extends Event> E dispatchEvent(E event) {
        for (EventPriority priority : EventPriority.values()) {
            this.events.tryEmitNext(Tuples.of(priority, event));
        }
        return event;
    }

    /**
     * Subscribes a handler to events of a specific type and priority.
     *
     * <p>When an event of the given type is dispatched with the specified priority,
     * the provided handler will be invoked. Handlers may choose to ignore cancelled events
     * by setting {@code ignoreCancelled} to {@code true}.</p>
     *
     * @param eventClass      the class of the event to listen for
     * @param priority        the priority level at which the handler should be invoked
     * @param ignoreCancelled whether to skip {@link CancelableEvent}s that are cancelled
     * @param handler         the logic to execute when the event occurs
     * @param <E>             the type of the event
     * @return a {@link Disposable} that can be used to unregister the handler
     */
    public <E extends Event> Disposable on(Class<E> eventClass, EventPriority priority, boolean ignoreCancelled, EventHandler<E> handler) {
        return this.handlers
                .filter(tuple -> tuple.getT1().equals(priority))
                .map(Tuple2::getT2)
                .filter(event -> event.getClass().isAssignableFrom(eventClass))
                .filter(event -> ignoreCancelled || !(event instanceof CancelableEvent cancelable) || !cancelable.isCanceled())
                .cast(eventClass)
                .subscribe(handler::handle);
    }

    /**
     * Subscribes a handler to events of a specific type with default priority ({@link EventPriority#NORMAL}).
     *
     * @param eventClass      the class of the event to listen for
     * @param ignoreCancelled whether to skip {@link CancelableEvent}s that are cancelled
     * @param handler         the event handling logic
     * @param <E>             the type of the event
     * @return a {@link Disposable} to unsubscribe the handler
     */
    public <E extends Event> Disposable on(Class<E> eventClass, boolean ignoreCancelled, EventHandler<E> handler) {
        return this.on(eventClass, EventPriority.NORMAL, ignoreCancelled, handler);
    }

    /**
     * Subscribes a handler to events of a specific type and priority,
     * excluding cancelled events if applicable.
     *
     * @param eventClass the class of the event to listen for
     * @param priority   the priority level for the handler
     * @param handler    the event handling logic
     * @param <E>        the type of the event
     * @return a {@link Disposable} to unsubscribe the handler
     */
    public <E extends Event> Disposable on(Class<E> eventClass, EventPriority priority, EventHandler<E> handler) {
        return this.on(eventClass, priority, false, handler);
    }

    /**
     * Subscribes a handler to events of a specific type with default settings.
     *
     * <p>Uses {@link EventPriority#NORMAL} and excludes cancelled events.</p>
     *
     * @param eventClass the class of the event to listen for
     * @param handler    the event handling logic
     * @param <E>        the type of the event
     * @return a {@link Disposable} to unsubscribe the handler
     */
    public <E extends Event> Disposable on(Class<E> eventClass, EventHandler<E> handler) {
        return this.on(eventClass, EventPriority.NORMAL, false, handler);
    }

    /**
     * Initializes the event bus after the application context is started by scanning
     * all beans implementing {@link Listener} and registering their {@link org.sbooster.webmorph.eventbus.annotation.EventHandler} methods.
     *
     * <p>This method uses reflection to locate all methods annotated with {@code @EventHandler},
     * validates them, and subscribes them to the appropriate event types based on their
     * parameter type and annotation metadata.</p>
     *
     * @param event the application start event triggering registration
     */
    @EventListener
    @SuppressWarnings("unchecked")
    private void init(ApplicationStartedEvent event) {
        Class<org.sbooster.webmorph.eventbus.annotation.EventHandler> annotation = org.sbooster.webmorph.eventbus.annotation.EventHandler.class;
        event.getApplicationContext().getBeansOfType(Listener.class, true, false).values()
                .forEach(listener -> Reflect.on(listener).methods(m -> m.isAnnotationPresent(annotation))
                        .forEach(method -> {
                            Parameter[] parameters = method.parameters();
                            if (parameters.length != 1) {
                                log.warn("Illegal arguments count or the provided argument is not an event");
                                return;
                            }
                            Arrays.stream(parameters)
                                    .map(Parameter::getType)
                                    .findFirst()
                                    .ifPresent(type -> {
                                        if (Event.class.isAssignableFrom(type)) {
                                            org.sbooster.webmorph.eventbus.annotation.EventHandler handler = method.annotation(annotation);
                                            on((Class<? extends Event>) type, handler.priority(),
                                                    handler.force(), method::invoke);
                                        }
                                    });
                        }));
    }
}
