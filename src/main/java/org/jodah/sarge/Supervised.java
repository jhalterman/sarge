package org.jodah.sarge;

/**
 * Defines a supervised type.
 * 
 * @author Jonathan Halterman
 */
public interface Supervised {
  /** Returns the plan to be used when supervising instances of this class. */
  Plan plan();
}
