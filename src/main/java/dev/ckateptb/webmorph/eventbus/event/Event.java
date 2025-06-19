package dev.ckateptb.webmorph.eventbus.event;

import dev.ckateptb.webmorph.WebMorph;
import dev.ckateptb.webmorph.eventbus.EventBus;

/**
 * Base class for all events used in the reactive EventBus system.
 *
 * <p>This class serves as the foundation for both cancellable and non-cancellable event types.
 * It supports dynamic dispatching and static registration of handlers based on event type.</p>
 *
 * <p>Each subclass of {@code Event} can be dispatched via {@link #dispatch()}, and can also
 * register listeners via static {@code on(...)} methods without requiring direct access to the {@link EventBus}.</p>
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
}
