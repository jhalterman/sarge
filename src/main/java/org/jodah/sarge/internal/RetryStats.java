package org.jodah.sarge.internal;

/**
 * Tracks retry related statistics for a supervised instance.
 * 
 * @author Jonathan Halterman
 */
public final class RetryStats {
  // Retry stats
  private final int maxRetries;
  private final long windowNanos;
  private long waitTime;
  private int retryCount;

  // Backoff stats
  private double backoffExponent = -1;
  private long initialRetryIntervalMillis;
  private long maxRetryIntervalMillis;
  private long retryWindowStartNanos;
  private boolean backoffReady;

  RetryStats(RetryDirective directive) {
    this.maxRetries = directive.getMaxRetries();
    windowNanos = directive.getRetryWindow().toNanos();

    if (directive.shouldBackoff()) {
      initialRetryIntervalMillis = directive.getInitialRetryInterval().toMillis();
      waitTime = initialRetryIntervalMillis;
      this.backoffExponent = directive.getBackoffExponent();
      maxRetryIntervalMillis = directive.getMaxRetryInterval().toMillis();
    }
  }

  /**
   * Determines whether a retry is allowed based on a simple window scheme. The window is held open
   * for the configured timeRange, and retries are allowed until maxRetries is exceeded within the
   * window. Once the window is closed it restarts and the retries are reset.
   * 
   * TODO implement a more sophisticated sliding window scheme
   * 
   * @return true if a retry is allowed
   */
  public boolean canRetry() {
    if (backoffExponent != -1 && backoffReady)
      waitTime = Math.min(maxRetryIntervalMillis, (long) (waitTime * backoffExponent));
    backoffReady = true;

    retryCount++;
    long now = System.nanoTime();
    long windowStart;

    if (retryWindowStartNanos == 0) {
      retryWindowStartNanos = now;
      windowStart = now;
    } else
      windowStart = retryWindowStartNanos;

    boolean insideWindow = (now - windowStart) <= windowNanos;
    if (insideWindow) {
      return maxRetries == -1 || retryCount <= maxRetries;
    } else {
      // Reset window
      resetBackoff();
      retryCount = 0;
      retryWindowStartNanos = now;
      return true;
    }
  }

  void resetBackoff() {
    backoffReady = false;
    waitTime = initialRetryIntervalMillis;
  }

  long getWaitTime() {
    return waitTime;
  }
}
