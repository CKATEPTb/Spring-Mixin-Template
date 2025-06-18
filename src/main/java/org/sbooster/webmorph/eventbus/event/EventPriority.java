package org.sbooster.webmorph.eventbus.event;

/**
 * Defines the execution order of event handlers within the EventBus system.
 *
 * <p>Handlers registered to an event can specify a priority to control their execution order.
 * Lower priority values are invoked earlier, and higher ones later. This allows fine-grained
 * coordination of how different listeners react to the same event, including interception,
 * modification, or cancellation.</p>
 *
 * <p>All handlers of a given priority level are executed in registration order.</p>
 *
 * <p>The default priority is {@link #NORMAL}.</p>
 */
public enum EventPriority {
    /**
     * The handler with this priority will be the <strong>first</strong> to receive the event.
     * Suitable for low-level filters or pre-validation logic.
     */
    LOWEST,
    /**
     * A low-priority handler that is invoked early, but after {@link #LOWEST}.
     */
    LOW,

    /**
     * Default priority. Used when no explicit priority is specified.
     */
    NORMAL,
    /**
     * A high-priority handler that is invoked late, but before {@link #HIGHEST}.
     */
    HIGH,
    /**
     * The handler with this priority will be the <strong>last</strong> to receive the event.
     * Suitable for logging, auditing, or final decision logic.
     */
    HIGHEST,
}
