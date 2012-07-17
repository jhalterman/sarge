package org.sarge.functional;

import org.sarge.Directive;
import org.sarge.Plan;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class UnhandledFailure extends AbstractFunctionalTest {
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
    Foo foo = sarge.supervise(Foo.class, UNHANDLED_PLAN);
    foo.doSomething();
  }
}
