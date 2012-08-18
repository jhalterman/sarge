package org.jodah.sarge.functional;

import org.jodah.sarge.Sarge;
import org.testng.annotations.BeforeMethod;

abstract class AbstractFunctionalTest {
  protected Sarge sarge;

  @BeforeMethod
  protected void beforeMethod() {
    sarge = new Sarge();
  }
}
