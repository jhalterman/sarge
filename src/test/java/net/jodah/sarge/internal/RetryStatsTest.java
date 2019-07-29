package net.jodah.sarge.internal;

import org.testng.annotations.Test;

import java.time.Duration;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Jonathan Halterman
 */
@Test
public class RetryStatsTest {
  public void shouldAllowRetryWhenAttemptsNotExceeded() throws Exception {
    RetryStats stats = new RetryStats(new RetryDirective(3, Duration.ofMillis(50)));
    assertTrue(stats.canRetry());
    stats.canRetry();
    stats.canRetry();
    Thread.sleep(75);
    assertTrue(stats.canRetry());
  }

  public void shouldAllowRetryWhenAttemptsExceededOutsideOfWindow() throws Exception {
    RetryStats stats = new RetryStats(new RetryDirective(3, Duration.ofMillis(50)));
    stats.canRetry();
    stats.canRetry();
    assertTrue(stats.canRetry());
    Thread.sleep(75);
    stats.canRetry();
    stats.canRetry();
    assertTrue(stats.canRetry());
  }

  public void shouldNotAllowRetryWhenAttemptsExceededWithinWindow() throws Exception {
    RetryStats stats = new RetryStats(new RetryDirective(3, Duration.ofMillis(50)));
    stats.canRetry();
    assertTrue(stats.canRetry());
    stats.canRetry();
    stats.canRetry();
    stats.canRetry();
    assertFalse(stats.canRetry());

    // Positive test after window reset
    Thread.sleep(75);
    assertTrue(stats.canRetry());
  }
}
