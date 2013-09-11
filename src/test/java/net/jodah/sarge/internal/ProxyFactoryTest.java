package net.jodah.sarge.internal;

import net.jodah.sarge.ProxyException;
import net.jodah.sarge.Sarge;

import org.testng.annotations.Test;

@Test
public class ProxyFactoryTest {
  private static final class Foo {
  }

  @Test(expectedExceptions = ProxyException.class)
  public void proxyForShouldThrowForRestrictedTypes() {
    ProxyFactory.proxyFor(Foo.class, new Sarge());
  }
}
