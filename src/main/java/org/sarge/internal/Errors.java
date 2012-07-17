package org.sarge.internal;

import java.util.ArrayList;
import java.util.List;


public final class Errors {
  private List<ErrorMessage> errors;

  public Errors() {
  }

  public static String format(String messageFormat, Object... arguments) {
    for (int i = 0; i < arguments.length; i++)
      arguments[i] = arguments[i]; // Errors.convert(arguments[i]);
    return String.format(messageFormat, arguments);
  }

  private Errors addMessage(Throwable cause, String message, Object... arguments) {
    addMessage(new ErrorMessage(format(message, arguments), cause));
    return this;
  }

  public Errors addMessage(ErrorMessage message) {
    if (errors == null)
      errors = new ArrayList<ErrorMessage>();
    errors.add(message);
    return this;
  }

  Errors errorEnhancingClass(Class<?> type, Throwable t) {
    return addMessage(t, "Failed to generate proxy class for %s", type);
  }

  Errors errorInstantiatingProxy(Class<?> type, Throwable t) {
    return addMessage(
        t,
        "Failed to instantiate proxied instance of %s. Ensure that %s has a non-private no-argument constructor.",
        type, type);
  }

  public ErrorsException toException() {
    return new ErrorsException(this);
  }
}
