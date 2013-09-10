package net.jodah.sarge;

import net.jodah.sarge.Sarge;

import org.testng.annotations.BeforeMethod;

public abstract class AbstractTest {
  protected Sarge sarge;

  @BeforeMethod
  protected void beforeMethod() {
    sarge = new Sarge();
  }
}
