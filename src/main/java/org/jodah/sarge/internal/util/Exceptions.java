package org.jodah.sarge.internal.util;

/**
 * Utilities for working with exceptions.
 * 
 * @author Jonathan Halterman
 */
public final class Exceptions {
  /**
   * A marker exception class that we look for in order to unwrap the exception into the user
   * exception, to provide a cleaner stack trace.
   */
  private static class UnhandledCheckedUserException extends RuntimeException {
    private static final long serialVersionUID = 0L;

    public UnhandledCheckedUserException(Throwable cause) {
      super(cause);
    }
  }

  private Exceptions() {
  }

  /** Returns a new unchecked exception wrapping the {@code cause}. */
  public static RuntimeException uncheck(Throwable cause) {
    return new UnhandledCheckedUserException(cause);
  }

  /** Throw <b>any</b> exception as a RuntimeException. */
  public static RuntimeException throwUnchecked(Throwable throwable) {
    Assert.notNull(throwable, "throwable");
    Exceptions.<RuntimeException>sneakyThrow(throwable);
    return null;
  }

  @SuppressWarnings("unchecked")
  private static <T extends Throwable> void sneakyThrow(Throwable t) throws T {
    throw (T) t;
  }
}
