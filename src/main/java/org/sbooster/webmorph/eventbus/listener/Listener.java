package org.sbooster.webmorph.eventbus.listener;

/**
 * Marker interface for classes that declare one or more methods annotated with {@link org.sbooster.webmorph.eventbus.annotation.EventHandler}.
 *
 * <p>Classes implementing this interface are automatically scanned by the EventBus system
 * to discover and register all eligible event-handling methods.</p>
 *
 * <p>Unlike {@link org.sbooster.webmorph.eventbus.event.EventHandler}, which defines a single
 * event consumer, this interface is used for grouping multiple annotated handler methods
 * within a single component.</p>
 *
 * <p>Implementing this interface is NOT optional!</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * @Service
 * public class UserEventListener implements Listener {
 *
 *     @EventHandler
 *     public void onUserJoin(UserJoinEvent event) {
 *         // handle join
 *     }
 *
 *     @EventHandler(priority = EventPriority.HIGH)
 *     public void onUserLeave(UserLeaveEvent event) {
 *         // handle leave
 *     }
 * }
 * }</pre>
 *
 * @see org.sbooster.webmorph.eventbus.annotation.EventHandler
 */

public interface Listener {
}
