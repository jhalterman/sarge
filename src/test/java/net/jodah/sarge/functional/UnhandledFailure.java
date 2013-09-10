package net.jodah.sarge.functional;

import net.jodah.sarge.AbstractTest;
import net.jodah.sarge.Directive;
import net.jodah.sarge.Plan;

import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class UnhandledFailure extends AbstractTest {
  private static final Plan UNHANDLED_PLAN = new Plan() {
    public Directive apply(Throwable cause) {
      return null;
    }
  };

  static class Foo {
    void doSomething() {
      throw new IllegalStateException();
    }
  }

  @Test(expectedExceptions = IllegalStateException.class)
  public void shouldRethrowOnUnhandledFailure() {
    Foo foo = sarge.supervised(Foo.class, UNHANDLED_PLAN);
    foo.doSomething();
  }
}
