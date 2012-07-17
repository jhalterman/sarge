package org.sarge.internal.util;

/**
 * @author Jonathan Halterman
 */
public final class Assert {
  private Assert() {
  }

  public static void isNull(Object object) {
    if (object != null)
      throw new IllegalArgumentException();
  }

  public static void isNull(Object object, String message) {
    if (object != null)
      throw new IllegalArgumentException(message);
  }

  public static void isTrue(boolean expression) {
    if (!expression)
      throw new IllegalArgumentException();
  }

  public static void isTrue(boolean expression, String errorMessage) {
    if (!expression)
      throw new IllegalArgumentException(errorMessage);
  }

  public static void isTrue(boolean expression, String errorMessage, Object... errorMessageArgs) {
    if (!expression)
      throw new IllegalArgumentException(String.format(errorMessage, errorMessageArgs));
  }

  public static <T> T notNull(T reference) {
    if (reference == null)
      throw new IllegalArgumentException();
    return reference;
  }

  public static <T> T notNull(T reference, String parameterName) {
    if (reference == null)
      throw new IllegalArgumentException(parameterName + " cannot be null");
    return reference;
  }

  public static void state(boolean expression) {
    if (!expression)
      throw new IllegalStateException();
  }

  public static void state(boolean expression, String message) {
    if (!expression)
      throw new IllegalStateException(message);
  }
}