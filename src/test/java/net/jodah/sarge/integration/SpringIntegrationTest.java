package net.jodah.sarge.integration;

import net.jodah.sarge.Directive;
import net.jodah.sarge.Plan;
import net.jodah.sarge.Plans;
import net.jodah.sarge.Sarge;
import net.jodah.sarge.SelfSupervisor;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

@Test
public class SpringIntegrationTest {
  private ApplicationContext appContext = new ClassPathXmlApplicationContext(new String[] {
      "sarge-beans.xml", "test-beans.xml" });
  private Sarge sarge = appContext.getBean(Sarge.class);

  static class Foo implements SelfSupervisor {
    void doSomething() {
      throw new IllegalStateException();
    }

    public Plan selfPlan() {
      return Plans.onFailure(IllegalStateException.class, Directive.Resume).make();
    }
  }

  public void shouldSuperviseSpringInstantiatedObject() {
    Foo foo = appContext.getBean(Foo.class);
    sarge.supervise(foo);
    foo.doSomething();
  }

  @Test(expectedExceptions = IllegalStateException.class)
  public void shouldSuperviseSpringInstantiatedObjectWithPlan() {
    Foo foo = appContext.getBean(Foo.class);
    sarge.supervise(foo, Plans.onFailure(IllegalStateException.class, Directive.Rethrow));
    foo.doSomething();
  }
}
