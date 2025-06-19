package dev.ckateptb.webmorph.eventbus.listener;

import dev.ckateptb.webmorph.eventbus.annotation.EventHandler;

/**
 * Marker interface for classes that declare one or more methods annotated with {@link EventHandler}.
 *
 * <p>Classes implementing this interface are automatically scanned by the EventBus system
 * to discover and register all eligible event-handling methods.</p>
 *
 * <p>Unlike {@link dev.ckateptb.webmorph.eventbus.event.EventHandler}, which defines a single
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
 * @see EventHandler
 */

public interface Listener {
}
