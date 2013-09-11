package net.jodah.sarge.internal;

import java.lang.reflect.Method;

import net.jodah.sarge.ProxyException;
import net.jodah.sarge.Sarge;
import net.jodah.sarge.SupervisedInterceptor;
import net.sf.cglib.core.DefaultNamingPolicy;
import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.NoOp;

/**
 * Produces proxied instances of supervisable types.
 * 
 * @author Jonathan Halterman
 */
public class ProxyFactory {
  private static final NamingPolicy NAMING_POLICY = new DefaultNamingPolicy() {
    @Override
    protected String getTag() {
      return "BySarge";
    }
  };

  private static final CallbackFilter METHOD_FILTER = new CallbackFilter() {
    public int accept(Method method) {
      return method.isBridge()
          || (method.getName().equals("finalize") && method.getParameterTypes().length == 0) ? 1
          : 0;
    }
  };

  /**
   * @throws ProxyException if the proxy for {@code type} cannot be generated or instantiated
   */
  public static <T> T proxyFor(Class<T> type, Sarge sarge) {
    Class<?> enhanced = null;

    try {
      enhanced = proxyClassFor(type);
      Enhancer.registerCallbacks(enhanced, new Callback[] {
          new CglibMethodInterceptor(new SupervisedInterceptor(sarge)), NoOp.INSTANCE });
      T result = type.cast(enhanced.newInstance());
      return result;
    } catch (Throwable t) {
      throw Errors.errorInstantiatingProxy(type, t);
    } finally {
      if (enhanced != null)
        Enhancer.registerCallbacks(enhanced, null);
    }
  }

  private static Class<?> proxyClassFor(Class<?> type) {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(type);
    enhancer.setUseFactory(false);
    enhancer.setUseCache(true);
    enhancer.setNamingPolicy(NAMING_POLICY);
    enhancer.setCallbackFilter(METHOD_FILTER);
    enhancer.setCallbackTypes(new Class[] { MethodInterceptor.class, NoOp.class });
    return enhancer.createClass();
  }
}
