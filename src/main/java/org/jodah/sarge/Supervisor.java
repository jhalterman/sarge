package org.jodah.sarge;

/**
 * Defines a type capable of supervising child objects.
 * 
 * @author Jonathan Halterman
 */
public interface Supervisor {
  /** Returns the plan to be used when supervising child objects. */
  Plan plan();
}
