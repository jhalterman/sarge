package org.jodah.sarge.functional;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import org.jodah.sarge.Plan;
import org.jodah.sarge.Plans;
import org.jodah.sarge.Supervisor;
import org.jodah.sarge.util.Duration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class HierarchicalSupervisionTest extends AbstractFunctionalTest {
  private static int counter;
  private static final Plan RETRY_PLAN = Plans.retryOn(Throwable.class, 3, Duration.mins(10))
                                              .make();
  private static final Plan ESCALATE_PLAN = Plans.escalateOn(Throwable.class)
                                                 .make();

  static class UberLevel implements Supervisor {
    public Plan plan() {
      return RETRY_PLAN;
    }
  }

  static class TopLevel implements Supervisor {
    public Plan plan() {
      return RETRY_PLAN;
    }
  }

  static class TopLevelEscalate implements Supervisor {
    public Plan plan() {
      return ESCALATE_PLAN;
    }
  }

  static class MidLevel implements Supervisor {
    public Plan plan() {
      return ESCALATE_PLAN;
    }
  }

  static class Child {
    void doSomething() {
      counter++;
      throw new RuntimeException();
    }
  }

  @BeforeMethod
  protected void beforeMethod() {
    super.beforeMethod();
    counter = 0;
  }

  public void shouldEscalateFailureToRetryPlan() {
    UberLevel u = new UberLevel();
    TopLevel t = new TopLevel();
    MidLevel m = new MidLevel();
    Child c = sarge.supervisable(Child.class);
    sarge.link(u, t);
    sarge.link(t, m);
    sarge.link(m, c);

    try {
      c.doSomething();
    } catch (RuntimeException e) {
      assertEquals(counter, 7);
    }
  }

  public void shouldThrowAfterRetriesExceeded() {
    TopLevel t = new TopLevel();
    Child c = sarge.supervisable(Child.class);
    sarge.link(t, c);

    try {
      c.doSomething();
      fail();
    } catch (Exception e) {
      assertEquals(counter, 4);
    }
  }

  @Test(expectedExceptions = RuntimeException.class)
  public void shouldEscalateFailuresToEscalatePlan() {
    TopLevelEscalate t = new TopLevelEscalate();
    MidLevel m = new MidLevel();
    Child c = sarge.supervisable(Child.class);
    sarge.link(t, m);
    sarge.link(m, c);
    c.doSomething();
  }
}
