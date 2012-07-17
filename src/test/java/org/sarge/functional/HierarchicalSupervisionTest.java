package org.sarge.functional;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.sarge.Plan;
import org.sarge.Plans;
import org.sarge.Supervisor;
import org.sarge.util.Duration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class HierarchicalSupervisionTest extends AbstractFunctionalTest {
  private static int counter;
  private static final Plan RETRY_PLAN = Plans.retryOn(Throwable.class, 3,
      Duration.of(10, TimeUnit.MINUTES))
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

  public void shouldEscalateFailureToRetryStrategy() {
    UberLevel u = new UberLevel();
    TopLevel t = new TopLevel();
    MidLevel m = new MidLevel();
    Child c = supervision.supervisable(Child.class);
    supervision.link(u, t);
    supervision.link(t, m);
    supervision.link(m, c);

    try {
      c.doSomething();
    } catch (RuntimeException e) {
      assertEquals(counter, 7);
    }
  }

  public void shouldThrowAfterRetriesExceeded() {
    TopLevel t = new TopLevel();
    Child c = supervision.supervisable(Child.class);
    supervision.link(t, c);

    try {
      c.doSomething();
      fail();
    } catch (Exception e) {
      assertEquals(counter, 4);
    }
  }

  @Test(expectedExceptions = RuntimeException.class)
  public void shouldEscalateFailuresToEscalateStrategy() {
    TopLevelEscalate t = new TopLevelEscalate();
    MidLevel m = new MidLevel();
    Child c = supervision.supervisable(Child.class);
    supervision.link(t, m);
    supervision.link(m, c);
    c.doSomething();
  }
}
