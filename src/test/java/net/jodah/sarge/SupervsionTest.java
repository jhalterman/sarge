package net.jodah.sarge;

import static org.testng.Assert.fail;
import net.jodah.sarge.Directive;
import net.jodah.sarge.Plan;
import net.jodah.sarge.Sarge;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class SupervsionTest {
  private Sarge supervision;

  private static final Plan RESUME_PLAN = new Plan() {
    public Directive apply(Throwable cause) {
      return Directive.Resume;
    }
  };

  static class Foo {
    void doSomething() {
      throw new RuntimeException();
    }

    Integer doSomethingWithReturn() {
      throw new RuntimeException();
    }
  }

  @BeforeMethod
  protected void beforeMethod() {
    supervision = new Sarge();
  }

  public void shouldThrowOnSupervisedWithStrategyWithoutAnnotation() {
    Foo foo = supervision.supervise(Foo.class, RESUME_PLAN);
    foo.doSomething();

    try {
      foo.doSomethingWithReturn();
      fail();
    } catch (Exception expected) {
    }
  }
}
