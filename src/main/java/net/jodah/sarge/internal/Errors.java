package net.jodah.sarge.internal;

import net.jodah.sarge.ProxyException;

public final class Errors {
  private Errors() {
  }

  static ProxyException errorInstantiatingProxy(Class<?> type, Throwable t) {
    return new ProxyException(
        t,
        "Failed to instantiate proxied instance of %s. Ensure that %s is not final, not private, and has a non-private no-argument constructor.",
        type, type);
  }
}
