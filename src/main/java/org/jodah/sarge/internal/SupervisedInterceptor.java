package org.jodah.sarge.internal;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.jodah.sarge.Directive;
import org.jodah.sarge.Plan;
import org.jodah.sarge.Supervisor;
import org.jodah.sarge.Lifecycle.PreRetry;

/**
 * Intercepts supervised object method invocations and applies a {@link Plan} for any failures.
 * 
 * @author Jonathan Halterman
 */
class SupervisedInterceptor implements MethodInterceptor {
  private final SupervisionRegistry registry;

  SupervisedInterceptor(SupervisionRegistry registry) {
    this.registry = registry;
  }

  public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy)
      throws Throwable {
    if (method.getDeclaringClass()
              .equals(Object.class)
        || (method.getName()
                  .equals("plan") && method.getParameterTypes().length == 0))
      return methodProxy.invokeSuper(proxy, args);

    Object supervised = proxy;
    Supervisor supervisor = registry.supervisorFor(supervised);
    Plan plan = supervisor == null ? registry.planFor(supervised) : supervisor.plan();
    Throwable cause = null;
    RetryStats retryStats = null;

    // Retry loop
    while (true) {
      try {
        if (cause != null && supervised instanceof PreRetry)
          ((PreRetry) supervised).preRetry(cause);

        Object result = methodProxy.invokeSuper(proxy, args);

        if (retryStats != null)
          retryStats.resetBackoff();
        return result;
      } catch (Throwable t) {
        cause = t;

        // Supervision hierarchy traversal
        while (true) {
          Directive directive = plan.apply(cause);
          boolean retry = directive instanceof RetryDirective;

          if (directive == null || Directive.Rethrow.equals(directive)) {
            throw t;
          } else if (Directive.Resume.equals(directive)) {
            if (method.getReturnType()
                      .equals(void.class))
              return null;
            throw t;
          } else if (retry || Directive.Escalate.equals(directive)) {
            if (retry) {
              RetryDirective retryDirective = (RetryDirective) directive;
              retryStats = RetryContextRegistry.contextFor(supervised, retryDirective).retryStats;
              if (retryStats.canRetry()) {
                if (retryDirective.shouldBackoff()) {
                  long waitTime = retryStats.getWaitTime();
                  Thread.sleep(waitTime);
                }
                break;
              }
            } else
              retryStats = null;

            if (supervisor == null)
              throw t;
            supervised = supervisor;
            supervisor = registry.supervisorFor(supervisor);
            if (supervisor == null)
              throw t;
            plan = supervisor.plan();
          }
        }
      }
    }
  }
}
