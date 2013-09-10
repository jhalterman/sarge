package net.jodah.sarge;

/**
 * A type capable of supervising itself.
 * 
 * @author Jonathan Halterman
 */
public interface SelfSupervisor {
  /** Returns the plan to be used when supervising itself. */
  Plan selfPlan();
}
