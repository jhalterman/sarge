package net.jodah.sarge.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.jodah.sarge.Plan;
import net.jodah.sarge.Supervisor;

/**
 * Statically stores and retrieves SupervisionContext instances.
 * 
 * @author Jonathan Halterman
 */
public class SupervisionRegistry {
  private final Map<Object, Supervisor> supervisors = new ConcurrentHashMap<Object, Supervisor>();
  private final Map<Object, Plan> plans = new ConcurrentHashMap<Object, Plan>();

  /** Returns the Plan for the {@code supervised}. */
  public Plan planFor(Object supervised) {
    return plans.get(supervised);
  }

  public void supervise(Object supervised, Plan strategy) {
    plans.put(supervised, strategy);
  }

  /**
   * @throws IllegalArgumentException if {@code supervised} is already supervised
   */
  public void supervise(Object supervised, Supervisor supervisor) {
    Supervisor existingSupervisor = supervisors.get(supervised);
    if (existingSupervisor != null)
      throw new IllegalArgumentException(supervised + " is already supervised");
    supervisors.put(supervised, supervisor);
  }

  /** Returns the supervisor for the {@code supervised}. */
  public Supervisor supervisorFor(Object supervised) {
    return supervisors.get(supervised);
  }

  /**
   * @throws IllegalArgumentException if {@code supervised} is not supervised
   */
  public void unsupervise(Object supervised) {
    if (plans.remove(supervised) == null && supervisors.remove(supervised) == null)
      throw new IllegalArgumentException(supervised + " is not supervised");
  }
}
