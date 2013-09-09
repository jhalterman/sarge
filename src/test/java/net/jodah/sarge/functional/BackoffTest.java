package net.jodah.sarge.functional;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import net.jodah.sarge.Plans;
import net.jodah.sarge.util.Duration;

import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "slow")
public class BackoffTest extends AbstractFunctionalTest {
  private static int counter;

  static class Foo {
    void doSomething() {
      counter++;
      throw new IllegalStateException();
    }
  }

  public void shouldBackoff() {
    Foo foo = sarge.supervise(Foo.class, Plans.retryOn(IllegalStateException.class, 5,
        Duration.inf(), Duration.millis(100), Duration.millis(800)));

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
