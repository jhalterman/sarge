package net.jodah.sarge;

/**
 * Plan for supervising objects.
 * 
 * @author Jonathan Halterman
 */
public interface Plan {
  /**
   * Applies the plan to the {@code failure} returning a {@link Directive}.
   * 
   * @param failure to handle
   * @return Directive to carry out
   */
  Directive apply(Throwable failure);
}
