package org.sbooster.webmorph.eventbus.event;

/**
 * Functional interface for handling events published through the EventBus system.
 *
 * <p>Implementations of this interface define the logic that should be executed when a specific type
 * of event is dispatched. The {@link #handle(Event)} method is invoked for each relevant event
 * instance, respecting the handler's priority and cancellation rules (if applicable).</p>
 *
 * <p>This interface may be implemented directly or used indirectly via annotated methods
 * (e.g., {@code @EventHandler}) that are discovered through reflection.</p>
 *
 * @param <E> the type of event this handler processes
 *
 * @see Event
 * @see CancelableEvent
 * @see org.sbooster.webmorph.eventbus.annotation.EventHandler
 */
public interface EventHandler<E extends Event> {

    /**
     * Handles a published event of the expected type.
     *
     * <p>This method is called automatically by the EventBus when an event matching
     * the handler’s signature is emitted.</p>
     *
     * @param event the event instance to handle
     */
    void handle(E event);
}
