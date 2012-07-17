package org.sarge.functional;

import org.sarge.Sarge;
import org.testng.annotations.BeforeMethod;

abstract class AbstractFunctionalTest {
  protected Sarge supervision;

  @BeforeMethod
  protected void beforeMethod() {
    supervision = new Sarge();
  }
}
