/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.jodah.sarge.internal;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.cglib.proxy.MethodProxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * CGlib method interceptor.
 * 
 * @author Jonathan Halterman
 */
public class CglibMethodInterceptor implements net.sf.cglib.proxy.MethodInterceptor {
  private static final Set<String> AOP_INTERNAL_CLASSES = new HashSet<String>(Arrays.asList(
      CglibMethodInterceptor.class.getName(), CglibMethodInvocation.class.getName(),
      MethodProxy.class.getName()));

  private final MethodInterceptor interceptor;

  private static class CglibMethodInvocation implements MethodInvocation {
    final Object proxy;
    final Method method;
    final Object[] args;
    final MethodProxy methodProxy;

    CglibMethodInvocation(final Object proxy, final Method method, final Object[] args,
        final MethodProxy methodProxy) {
      this.proxy = proxy;
      this.method = method;
      this.args = args;
      this.methodProxy = methodProxy;
    }

    public Object[] getArguments() {
      return args;
    }

    public Method getMethod() {
      return method;
    }

    public AccessibleObject getStaticPart() {
      return method;
    }

    public Object getThis() {
      return proxy;
    }

    public Object proceed() throws Throwable {
      try {
        return methodProxy.invokeSuper(proxy, args);
      } catch (Throwable t) {
        pruneStacktrace(t);
        throw t;
      }
    }
  }

  public CglibMethodInterceptor(MethodInterceptor interceptor) {
    this.interceptor = interceptor;
  }

  /**
   * Removes AOP related stack frames from the trace.
   */
  private static void pruneStacktrace(Throwable throwable) {
    for (Throwable t = throwable; t != null; t = t.getCause()) {
      StackTraceElement[] stackTrace = t.getStackTrace();
      List<StackTraceElement> pruned = new ArrayList<StackTraceElement>();
      for (StackTraceElement element : stackTrace) {
        String className = element.getClassName();
        if (!AOP_INTERNAL_CLASSES.contains(className) && !className.contains("$EnhancerBySarge$"))
          pruned.add(element);
      }

      t.setStackTrace(pruned.toArray(new StackTraceElement[pruned.size()]));
    }
  }

  public Object intercept(final Object proxy, final Method method, final Object[] args,
      final MethodProxy methodProxy) throws Throwable {
    return interceptor.invoke(new CglibMethodInvocation(proxy, method, args, methodProxy));
  }
}
