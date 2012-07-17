package org.sarge.internal;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.sarge.internal.RetryDirective;
import org.sarge.internal.RetryStats;
import org.sarge.util.Duration;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class RetryStatsTest {
  public void shouldAllowRetryWhenAttemptsNotExceeded() throws Exception {
    RetryStats stats = new RetryStats(new RetryDirective(3, Duration.millis(50)));
    assertTrue(stats.canRetry());
    stats.canRetry();
    stats.canRetry();
    Thread.sleep(75);
    assertTrue(stats.canRetry());
  }

  public void shouldAllowRetryWhenAttemptsExceededOutsideOfWindow() throws Exception {
    RetryStats stats = new RetryStats(new RetryDirective(3, Duration.millis(50)));
    stats.canRetry();
    stats.canRetry();
    assertTrue(stats.canRetry());
    Thread.sleep(75);
    stats.canRetry();
    stats.canRetry();
    assertTrue(stats.canRetry());
  }

  public void shouldNotAllowRetryWhenAttemptsExceededWithinWindow() throws Exception {
    RetryStats stats = new RetryStats(new RetryDirective(3, Duration.millis(50)));
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
