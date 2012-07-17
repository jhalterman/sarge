package org.sarge;

import org.sarge.Directive;
import org.sarge.PlanMaker;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class PlanMakerTest {
  @SuppressWarnings("unchecked")
  @Test(expectedExceptions = IllegalStateException.class)
  public void shouldRejectDuplicateThrowables() {
    PlanMaker builder = new PlanMaker();
    builder.addDirective(Directive.Escalate, IllegalStateException.class);
    builder.addDirective(Directive.Escalate, IllegalStateException.class);
  }
}
