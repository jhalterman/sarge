package net.jodah.sarge.functional;

import static org.testng.Assert.fail;
import net.jodah.sarge.Directive;
import net.jodah.sarge.Plans;

import org.testng.annotations.Test;

@Test
public class OrderedFailureHandling extends AbstractFunctionalTest {
  static class Foo {
    void doSomething() {
      throw new IllegalStateException();
    }

    void doSomethingElse() {
      throw new NullPointerException();
    }
  }

  public void shouldHandleFailuresInOrderDeclaredInPlan() {
    Foo foo = sarge.supervise(Foo.class,
        Plans.onFailure(IllegalStateException.class, Directive.Rethrow)
             .onFailure(Exception.class, Directive.Resume));

    try {
      foo.doSomething();
      fail();
    } catch (IllegalStateException expected) {
    }

    foo.doSomethingElse();
  }
}
