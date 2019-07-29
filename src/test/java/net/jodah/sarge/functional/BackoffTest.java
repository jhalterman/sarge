package net.jodah.sarge.functional;

import net.jodah.sarge.AbstractTest;
import net.jodah.sarge.Plans;
import org.testng.annotations.Test;

import java.time.Duration;

import static org.testng.Assert.*;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "slow")
public class BackoffTest extends AbstractTest {
  private static int counter;

  static class Foo {
    void doSomething() {
      counter++;
      throw new IllegalStateException();
    }
  }

  public void shouldBackoff() {
    Foo foo = sarge.supervised(Foo.class, Plans.retryOn(IllegalStateException.class, 5,
        Duration.ofDays(1), Duration.ofMillis(100), Duration.ofMillis(800)));

    long startTime = System.currentTimeMillis();

    try {
      foo.doSomething();
      fail();
    } catch (IllegalStateException expected) {
    }

    assertEquals(counter, 6);
    assertTrue(System.currentTimeMillis() - startTime > 2300);
  }
}
