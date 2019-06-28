package net.jodah.sarge.internal;

import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

import net.jodah.sarge.Sarge;

@Test
public class ProxyFactoryTest
{
  private static final class Foo
  {
  }

  private class Dummy
  {
    private final String s;
    private final Integer i;

    public Dummy(String s, Integer i)
    {
      this.s = s;
      this.i = i;
    }
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void proxyForShouldThrowForRestrictedTypes()
  {
    ProxyFactory.proxyFor(Foo.class, new Sarge());
  }

  @Test
  public void proxyForWithArguments()
  {
    Dummy dummy = ProxyFactory.proxyFor(Dummy.class, new Object[]
    {
      "Test", 1
    }, new Sarge());
    
    assertNotNull(dummy);
  }
}
