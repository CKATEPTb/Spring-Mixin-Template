package dev.ckateptb.webmorph.eventbus.event;

import dev.ckateptb.webmorph.eventbus.annotation.EventHandler;
import lombok.Getter;
import lombok.Setter;

/**
 * An abstract base class for events that can be conditionally cancelled by event handlers.
 *
 * <p>This class extends {@link Event} by introducing a {@code canceled} flag, which allows
 * listeners to interrupt or halt further processing of the event. It is commonly used
 * when an operation should be vetoable, such as auth, validation, or user-driven actions.</p>
 *
 * <p>Event handlers can check the cancellation state using {@link #isCanceled()},
 * and may invoke {@link #setCanceled(boolean)} to modify it. By default, the event is not cancelled.</p>
 *
 * <p>When an event is cancelled, further non-{@code force=true} handlers are skipped during propagation.</p>
 *
 * @see Event
 * @see EventHandler#force()
 */
@Getter
@Setter
public abstract class CancelableEvent extends Event {
    /**
     * Indicates whether this event has been cancelled.
     * <p>If {@code true}, subsequent non-forced handlers will be skipped.</p>
     */
    private boolean canceled;
}