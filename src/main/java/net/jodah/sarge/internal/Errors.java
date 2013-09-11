package net.jodah.sarge.internal;

public final class Errors {
  private Errors() {
  }

  static IllegalArgumentException errorInstantiatingProxy(Class<?> type, Throwable t) {
    return new IllegalArgumentException(
        String.format(
            "Failed to instantiate proxied instance of %s. Ensure that %s is not final, not private, and has a non-private no-argument constructor.",
            type, type), t);
  }
}
