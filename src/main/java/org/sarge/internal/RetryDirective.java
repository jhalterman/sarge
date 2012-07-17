package org.sarge.internal;

import org.sarge.Directive;
import org.sarge.util.Duration;

/**
 * Directive that encapsulates retry information.
 * 
 * @author Jonathan Halterman
 */
public class RetryDirective extends Directive {
  private final int maxRetries;
  private final Duration retryWindow;
  private final boolean backoff;
  private Duration initialRetryInterval;
  private double backoffExponent;
  private Duration maxRetryInterval;

  public RetryDirective(int maxRetries, Duration retryWindow) {
    this.maxRetries = maxRetries;
    this.retryWindow = retryWindow;
    backoff = false;
  }

  public RetryDirective(int maxRetries, Duration retryWindow, Duration initialRetryInterval,
      double backoffExponent, Duration maxRetryInterval) {
    this.maxRetries = maxRetries;
    this.retryWindow = retryWindow;
    backoff = true;
    this.initialRetryInterval = initialRetryInterval;
    this.backoffExponent = backoffExponent;
    this.maxRetryInterval = maxRetryInterval;
  }

  public double getBackoffExponent() {
    return backoffExponent;
  }

  public Duration getInitialRetryInterval() {
    return initialRetryInterval;
  }

  public int getMaxRetries() {
    return maxRetries;
  }

  public Duration getMaxRetryInterval() {
    return maxRetryInterval;
  }

  public Duration getRetryWindow() {
    return retryWindow;
  }

  public boolean shouldBackoff() {
    return backoff;
  }

  @Override
  public String toString() {
    return "Retry Directive";
  }
}