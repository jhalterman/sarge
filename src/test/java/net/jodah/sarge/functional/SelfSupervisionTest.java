package net.jodah.sarge.functional;

import net.jodah.sarge.AbstractTest;
import net.jodah.sarge.Directive;
import net.jodah.sarge.Plan;
import net.jodah.sarge.SelfSupervisor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * @author Jonathan Halterman
 */
@Test
public class SelfSupervisionTest extends AbstractTest {
  private static int counter;
  private static final Plan RETRY_PLAN = new Plan() {
    public Directive apply(Throwable cause) {
      return Directive.Retry(3, Duration.ofMinutes(10));
    }
  };
  private static final Plan ESCALATE_PLAN = new Plan() {
    public Directive apply(Throwable cause) {
      return Directive.Escalate;
    }
  };
  private static final Plan RETHROW_PLAN = new Plan() {
    public Directive apply(Throwable cause) {
      return Directive.Rethrow;
    }
  };
  private static final Plan RESUME_PLAN = new Plan() {
    public Directive apply(Throwable cause) {
      return Directive.Resume;
    }
  };

  static class Foo {
    void doSomething() {
      counter++;
      throw new IllegalStateException();
    }

    void doSomethingElse(AtomicInteger v) {
      v.set(v.get() + 1);
    }

    void doSomethingEscalated() {
      counter++;
      throw new IllegalStateException();
    }

    void throwIt(BinaryException e) {
      if (e.rethrow) {
        e.rethrow = false;
        throw e;
      }
    }
  }

  static class FooEscalate implements SelfSupervisor {
    public Plan selfPlan() {
      return ESCALATE_PLAN;
    }

    void doSomethingEscalated() {
      counter++;
      throw new IllegalStateException();
    }
  }

  static class FooShortRetry implements SelfSupervisor {
    public Plan selfPlan() {
      return new Plan() {
        public Directive apply(Throwable cause) {
          return Directive.Retry(2, Duration.ofMillis(100));
        }
      };
    }

    void throwIt(BinaryException e) {
      if (e.rethrow) {
        e.rethrow = false;
        throw e;
      }
    }
  }

  @SuppressWarnings("serial")
  static class BinaryException extends RuntimeException {
    boolean rethrow = true;
  }

  @BeforeMethod
  protected void beforeMethod() {
    super.beforeMethod();
    counter = 0;
  }

  public void shouldRetry() {
    Foo foo = sarge.supervised(Foo.class, RETRY_PLAN);

    try {
      foo.doSomething();
    } catch (IllegalStateException e) {
      assertEquals(counter, 4);
    }
  }

  public void shouldEscalate() {
    FooEscalate foo = sarge.supervised(FooEscalate.class);

    try {
      foo.doSomethingEscalated();
    } catch (IllegalStateException e) {
      assertEquals(counter, 1);
    }
  }

  public void shouldRetryWithArguments() {
    Foo foo = sarge.supervised(Foo.class, RETRY_PLAN);
    AtomicInteger i = new AtomicInteger(0);

    try {
      foo.doSomethingElse(i);
    } catch (IllegalStateException e) {
      assertEquals(i.get(), 4);
    }
  }

  public void shouldTrackFailuresForSameObjectAcrossSeparateInvocations() {
    Foo foo = sarge.supervised(Foo.class, RETRY_PLAN);
    foo.throwIt(new BinaryException());
    foo.throwIt(new BinaryException());
    foo.throwIt(new BinaryException());

    try {
      foo.throwIt(new BinaryException());
      fail();
    } catch (BinaryException expected) {
    }
  }

  public void shouldTrackFailuresForSeparateObjects() {
    Foo foo1 = sarge.supervised(Foo.class, RETRY_PLAN);
    Foo foo2 = sarge.supervised(Foo.class, RETRY_PLAN);

    foo1.throwIt(new BinaryException());
    foo1.throwIt(new BinaryException());
    foo1.throwIt(new BinaryException());

    foo2.throwIt(new BinaryException());
    foo2.throwIt(new BinaryException());
    foo2.throwIt(new BinaryException());

    try {
      foo1.throwIt(new BinaryException());
      fail();
    } catch (BinaryException expected) {
    }

    try {
      foo2.throwIt(new BinaryException());
      fail();
    } catch (BinaryException expected) {
    }
  }

  public void shouldAllowRetryWhenWindowExceeded() throws Exception {
    FooShortRetry foo = sarge.supervised(FooShortRetry.class);
    foo.throwIt(new BinaryException());
    foo.throwIt(new BinaryException());
    try {
      foo.throwIt(new BinaryException());
      fail();
    } catch (BinaryException expected) {
    }

    Thread.sleep(100);

    // Should succeed since retry window has been exceeded
    foo.throwIt(new BinaryException());
  }

  @Test(expectedExceptions = IllegalStateException.class)
  public void shouldRethrowOnFailure() {
    Foo foo = sarge.supervised(Foo.class, RETHROW_PLAN);
    foo.doSomething();
  }

  public void shouldResumeOnFailure() {
    Foo foo = sarge.supervised(Foo.class, RESUME_PLAN);
    foo.doSomething();
  }
}
