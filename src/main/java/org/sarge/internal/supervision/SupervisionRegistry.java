package org.sarge.internal.supervision;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.sarge.Plan;
import org.sarge.Supervisor;

/**
 * Statically stores and retrieves SupervisionContext instances.
 * 
 * @author Jonathan Halterman
 */
public class SupervisionRegistry {
  private final Map<Object, Supervisor> SUPERVISORS = new ConcurrentHashMap<Object, Supervisor>();
  private final Map<Object, Plan> SELF_SUPERVISION = new ConcurrentHashMap<Object, Plan>();

  /** Returns the SupervisionStrategy for the {@code selfSupervised}. */
  public Plan selfSupervisionFor(Object selfSupervised) {
    return SELF_SUPERVISION.get(selfSupervised);
  }

  public void supervise(Object supervised, Plan strategy) {
    SELF_SUPERVISION.put(supervised, strategy);
  }

  /**
   * @throws IllegalArgumentException if {@code supervised} is already supervised
   */
  public void supervise(Object supervised, Supervisor supervisor) {
    Supervisor existingSupervisor = SUPERVISORS.get(supervised);
    if (existingSupervisor != null)
      throw new IllegalArgumentException(supervised + " is already supervised");
    SUPERVISORS.put(supervised, supervisor);
  }

  /** Returns the supervisor for the {@code supervised}. */
  public Supervisor supervisorFor(Object supervised) {
    return SUPERVISORS.get(supervised);
  }

  /**
   * @throws IllegalArgumentException if {@code supervised} is not supervised
   */
  public void unsupervise(Object supervised) {
    if (SELF_SUPERVISION.remove(supervised) == null && SUPERVISORS.remove(supervised) == null)
      throw new IllegalArgumentException(supervised + " is not supervised");
  }
}
