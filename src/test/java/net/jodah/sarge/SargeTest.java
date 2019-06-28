package net.jodah.sarge;

import static org.testng.Assert.fail;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class SargeTest {
  private Sarge sarge;

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
    sarge = new Sarge();
  }

  public void shouldThrowOnSupervisedWithStrategyWithoutAnnotation() {
    Foo foo = sarge.supervised(Foo.class, RESUME_PLAN);
    foo.doSomething();

    try {
      foo.doSomethingWithReturn();
      fail();
    } catch (Exception expected) {
    }
  }
}
