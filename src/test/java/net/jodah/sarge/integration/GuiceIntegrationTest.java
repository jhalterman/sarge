package net.jodah.sarge.integration;

import net.jodah.sarge.Directive;
import net.jodah.sarge.Plan;
import net.jodah.sarge.Plans;
import net.jodah.sarge.Sarge;
import net.jodah.sarge.SelfSupervisor;

import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;

@Test
public class GuiceIntegrationTest {
  private Sarge sarge = new Sarge();
  private Injector injector = Guice.createInjector(new AbstractModule() {
    @Override
    protected void configure() {
      bindInterceptor(Matchers.subclassesOf(SelfSupervisor.class), Matchers.any(),
          sarge.interceptor());
    }
  });

  static class Foo implements SelfSupervisor {
    void doSomething() {
      throw new IllegalStateException();
    }

    public Plan selfPlan() {
      return Plans.onFailure(IllegalStateException.class, Directive.Resume).make();
    }
  }

  public void shouldSuperviseGuiceInstantiatedObject() {
    Foo foo = injector.getInstance(Foo.class);
    sarge.supervise(foo);
    foo.doSomething();
  }

  @Test(expectedExceptions = IllegalStateException.class)
  public void shouldLinkGuiceInstantiatedObject() {
    Foo foo = injector.getInstance(Foo.class);
    sarge.link(foo, Plans.onFailure(IllegalStateException.class, Directive.Rethrow));
    foo.doSomething();
  }
}
