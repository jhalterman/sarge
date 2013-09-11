package net.jodah.sarge.internal;

/**
 * Maintains context for a supervised object capable of having failures retried.
 * 
 * @author Jonathan Halterman
 */
public class RetryContext {
  public final RetryDirective directive;
  public final RetryStats retryStats;

  RetryContext(RetryDirective directive, RetryStats retryStats) {
    this.directive = directive;
    this.retryStats = retryStats;
  }
}
