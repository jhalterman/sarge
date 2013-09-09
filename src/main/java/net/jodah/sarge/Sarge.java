package net.jodah.sarge;

import net.jodah.sarge.internal.ProxyFactory;
import net.jodah.sarge.internal.SupervisionRegistry;
import net.jodah.sarge.internal.util.Assert;

/**
 * Maintains supervision information and provides supervised objects.
 * 
 * @author Jonathan Halterman
 */
public class Sarge {
  private final SupervisionRegistry registry = new SupervisionRegistry();
  private final ProxyFactory proxyFactory = new ProxyFactory(registry);

  /**
   * Links the {@code supervisor} to the {@code supervised} object, forming a supervision tree.
   * 
   * @throws NullPointerException if {@code supervised} or {@code supervisor} are null
   * @throws IllegalArgumentException if {@code supervised} is already supervised
   */
  public <T extends Supervisor> void link(T supervisor, Object supervised) {
    Assert.notNull(supervisor, "supervisor");
    Assert.notNull(supervised, "supervised");
    registry.supervise(supervised, supervisor);
  }

  /**
   * Returns an instance of {@code supervisableType} capable of being supervised.
   * 
   * @throws NullPointerException if {@code supervisableType} is null
   */
  public <T> T supervisable(Class<T> supervisableType) {
    Assert.notNull(supervisableType, "supervisableType");
    return proxyFactory.proxyFor(supervisableType);
  }

  /**
   * Returns a supervised instance of the type, with supervision dictated by the {@code supervisor}
   * 's supervision strategy.
   * 
   * @throws NullPointerException if {@code supervised} or {@code supervisor} are null
   * @throws ProvisionException if there was a runtime failure while providing an instance
   */
  public <C, S extends Supervisor> C supervise(Class<C> supervisedType, S supervisor) {
    Assert.notNull(supervisedType, "supervisedType");
    Assert.notNull(supervisor, "supervisor");
    C supervised = proxyFactory.proxyFor(supervisedType);
    link(supervisor, supervised);
    return supervised;
  }

  /**
   * Returns a supervised instance of the type, with supervision dictated by the Supervised type's
   * {@code supervisionStrategy}.
   * 
   * @throws NullPointerException if {@code supervisedType} is null
   * @throws ProvisionException if there was a runtime failure while providing an instance
   */
  public <T extends Supervised> T supervise(Class<T> supervisedType) {
    Assert.notNull(supervisedType, "supervisedType");
    T supervised = proxyFactory.proxyFor(supervisedType);
    link(supervised, supervised.plan());
    return supervised;
  }

  /**
   * Returns a supervised instance of the type, with supervision dictated by the {@code plan}.
   * 
   * @throws NullPointerException if {@code supervisedType} or {@code plan} are null
   * @throws ProvisionException if there was a runtime failure while providing an instance
   */
  public <T> T supervise(Class<T> supervisedType, Plan plan) {
    Assert.notNull(supervisedType, "supervisedType");
    Assert.notNull(plan, "plan");
    T supervised = proxyFactory.proxyFor(supervisedType);
    link(supervised, plan);
    return supervised;
  }

  /**
   * Returns a supervised instance of the type, with supervision dictated by the {@code planMaker}'s
   * plan.
   * 
   * @throws NullPointerException if {@code supervisedType} or {@code planMaker} are null
   * @throws ProvisionException if there was a runtime failure while providing an instance
   */
  public <T> T supervise(Class<T> supervisedType, PlanMaker planMaker) {
    Assert.notNull(planMaker, "planMaker");
    return supervise(supervisedType, planMaker.make());
  }

  /**
   * Unlinks the {@code supervised} object from its supervisor.
   * 
   * @throws NullPointerException if {@code supervised} is null
   * @throws IllegalStateException if {@code supervised} is not supervised
   */
  public void unlink(Object supervised) {
    Assert.notNull(supervised, "supervised");
    registry.unsupervise(supervised);
  }

  /**
   * Links the {@code plan} to the {@code supervised} object, forming a supervision tree.
   * 
   * @throws IllegalArgumentException if {@code supervised} is already supervised
   */
  private void link(Object supervised, Plan plan) {
    registry.supervise(supervised, plan);
  }
}
