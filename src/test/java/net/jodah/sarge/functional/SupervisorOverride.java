package net.jodah.sarge.functional;

import org.testng.annotations.Test;

import net.jodah.sarge.AbstractTest;
import net.jodah.sarge.Directive;
import net.jodah.sarge.Plan;
import net.jodah.sarge.SelfSupervisor;
import net.jodah.sarge.Supervisor;

/**
 * @author Jonathan Halterman
 */
@Test
public class SupervisorOverride extends AbstractTest {
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

  static class Child implements SelfSupervisor {
    public Plan selfPlan() {
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
    Child child = sarge.supervised(Child.class);
    sarge.supervise(child, new Parent());
    child.doSomething();
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void shouldNotAllowSelfSupervisionOverride() {
    Child child = sarge.supervised(Child.class, new Parent(), new Object[]{});
    sarge.supervise(child, new Parent());
  }
}
