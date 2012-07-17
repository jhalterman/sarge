package org.sarge.functional;

import org.sarge.Directive;
import org.sarge.Plan;
import org.sarge.Supervised;
import org.sarge.Supervisor;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class SupervisorOverride extends AbstractFunctionalTest {
  private static final Plan RETHROW_STRATEGY = new Plan() {
    public Directive apply(Throwable cause) {
      return Directive.Rethrow;
    }
  };
  private static final Plan RESUME_STRATEGY = new Plan() {
    public Directive apply(Throwable cause) {
      return Directive.Resume;
    }
  };

  static class Child implements Supervised {
    public Plan plan() {
      return RETHROW_STRATEGY;
    }

    void doSomething() {
      throw new RuntimeException();
    }
  }

  static class Parent implements Supervisor {
    public Plan plan() {
      return RESUME_STRATEGY;
    }
  }

  public void shouldOverrideSelfSupervisionWithParentSupervision() {
    Child child = supervision.supervise(Child.class);
    supervision.link(new Parent(), child);
    child.doSomething();
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void shouldNotAllowSelfSupervisionOverride() {
    Child child = supervision.supervise(Child.class, new Parent());
    supervision.link(new Parent(), child);
  }
}
