package org.jodah.sarge.functional;

import org.jodah.sarge.Directive;
import org.jodah.sarge.Plan;
import org.jodah.sarge.Supervised;
import org.jodah.sarge.Supervisor;
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
    Child child = sarge.supervise(Child.class);
    sarge.link(new Parent(), child);
    child.doSomething();
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void shouldNotAllowSelfSupervisionOverride() {
    Child child = sarge.supervise(Child.class, new Parent());
    sarge.link(new Parent(), child);
  }
}
