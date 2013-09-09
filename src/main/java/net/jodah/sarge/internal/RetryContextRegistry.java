package net.jodah.sarge.internal;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Statically stores and retrieves RetryContext instances.
 * 
 * @author Jonathan Halterman
 */
public class RetryContextRegistry {
  static final Map<Object, RetryContext> CONTEXTS = new WeakHashMap<Object, RetryContext>();

  static RetryContext contextFor(Object supervised, RetryDirective directive) {
    // DCL
    RetryContext context = CONTEXTS.get(supervised);
    if (context == null) {
      synchronized (CONTEXTS) {
        context = CONTEXTS.get(supervised);
        if (context == null) {
          RetryStats retryStats = new RetryStats(directive);
          context = new RetryContext(directive, retryStats);
          CONTEXTS.put(supervised, context);
        }
      }
    }

    return context;
  }
}
