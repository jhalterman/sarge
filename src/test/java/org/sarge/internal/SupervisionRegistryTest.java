package org.sarge.internal;

import org.sarge.internal.SupervisionRegistry;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class SupervisionRegistryTest {
  private SupervisionRegistry registry;

  static class Foo {
  }

  @BeforeMethod
  protected void beforeMethod() {
    registry = new SupervisionRegistry();
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void shouldThrowOnRemoveUnsupervised() {
    registry.unsupervise(new Foo());
  }
}
