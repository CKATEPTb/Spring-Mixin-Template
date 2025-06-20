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
 * <p>Event handlers can check the cancellation state using getter for {@link #canceled},
 * and may invoke setter for {@link #canceled} to modify it. By default, the event is not cancelled.</p>
 *
 * <p>When an event is cancelled, further non-{@code force=true} handlers are skipped during propagation.</p>
 *
 * @see Event
 * @see EventHandler#force()
 */
@Setter
@Getter
public abstract class CancelableEvent extends Event {
    /**
     * Indicates whether this event has been cancelled.
     * <p>If {@code true}, subsequent non-forced handlers will be skipped.</p>
     * -- GETTER --
     *  Returns the cancellation state of this event.
     *
     *
     * -- SETTER --
     *  Sets the cancellation state of this event.
     *  <p>
     *  If set to
     * , this will prevent further non-forced handlers
     *  from receiving the event during propagation.
     *  </p>
     *
     @return {@code true} if the event is canceled; {@code false} otherwise
      * @param canceled whether the event should be canceled

     */
    private boolean canceled;

}