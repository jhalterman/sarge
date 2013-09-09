package net.jodah.sarge;

import net.jodah.sarge.internal.RetryDirective;
import net.jodah.sarge.util.Duration;

/**
 * Determine how failures should be handled.
 * 
 * @author Jonathan Halterman
 */
public class Directive {
  /**
   * Resume supervision, re-throwing the failure from the point of invocation if a result is
   * expected.
   */
  public static final Directive Resume = new Directive() {
    @Override
    public String toString() {
      return "Resume Directive";
    }
  };

  /** Re-throws the failure from the point of invocation. */
  public static final Directive Rethrow = new Directive() {
    @Override
    public String toString() {
      return "Rethrow Directive";
    }
  };

  /** Escalates the failure to the supervisor of the supervisor. */
  public static final Directive Escalate = new Directive() {
    @Override
    public String toString() {
      return "Escalate Directive";
    }
  };

  /** Retries the method invocation. */
  public static Directive Retry(int maxRetries, Duration retryWindow) {
    return new RetryDirective(maxRetries, retryWindow);
  }

  /** Retries the method invocation. */
  public static Directive Retry(int maxRetries, Duration retryWindow,
      Duration initialRetryInterval, double backoffExponent, Duration maxRetryInterval) {
    return new RetryDirective(maxRetries, retryWindow, initialRetryInterval, backoffExponent,
        maxRetryInterval);
  }
}
