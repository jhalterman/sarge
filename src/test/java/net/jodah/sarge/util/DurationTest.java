package net.jodah.sarge.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import net.jodah.sarge.util.Duration;

import org.testng.annotations.Test;

@Test
public class DurationTest {
  public void testPattern() {
    assertTrue(Duration.PATTERN.matcher("1s").matches());
  }

  public void testOf() {
    assertEquals(Duration.seconds(1), Duration.of("1s"));
    assertEquals(Duration.seconds(1), Duration.of("1 s"));
    assertEquals(Duration.seconds(10), Duration.of("10 seconds"));
  }
}
