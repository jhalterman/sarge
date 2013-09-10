package net.jodah.sarge;

import net.jodah.sarge.internal.ProxyFactory;
import net.jodah.sarge.internal.SupervisedInterceptor;
import net.jodah.sarge.internal.SupervisionRegistry;
import net.jodah.sarge.internal.util.Assert;

import org.aopalliance.intercept.MethodInterceptor;

import com.google.inject.ProvisionException;

/**
 * Maintains supervision information and provides supervised objects.
 * 
 * @author Jonathan Halterman
 */
public class Sarge {
  private final SupervisionRegistry registry = new SupervisionRegistry();
  private final ProxyFactory proxyFactory = new ProxyFactory(registry);

  /**
   * Returns a method interceptor that can be used to integrate Sarge with 3rd party libraries.
   */
  public MethodInterceptor interceptor() {
    return new SupervisedInterceptor(registry);
  }

  /**
   * Links the {@code supervised} object to the {@code plan}, forming a supervision tree.
   * 
   * @throws NullPointerException if {@code supervised} or {@code plan} are null
   * @throws IllegalArgumentException if {@code supervised} is already supervised
   */
  public void link(Object supervised, Plan plan) {
    Assert.notNull(supervised, "supervised");
    Assert.notNull(plan, "plan");
    registry.supervise(supervised, plan);
  }

  /**
   * Links the {@code supervised} object to the {@code planMaker}'s plan, forming a supervision
   * tree.
   * 
   * @throws NullPointerException if {@code supervised} or {@code planMaker} are null
   * @throws IllegalArgumentException if {@code supervised} is already supervised
   */
  public void link(Object supervised, PlanMaker planMaker) {
    Assert.notNull(supervised, "supervised");
    Assert.notNull(planMaker, "planMaker");
    registry.supervise(supervised, planMaker.make());
  }

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
   * Returns an instance of the {@code type} that is capable of being supervised by calling one of
   * the link() methods.
   * 
   * @throws NullPointerException if {@code type} is null
   */
  public <T> T supervisable(Class<T> type) {
    Assert.notNull(type, "type");
    return proxyFactory.proxyFor(type);
  }

  /**
   * Supervises the {@code supervisable} object by {@link #link(Object, Plan) linking} it to its
   * {@link SelfSupervisor#selfPlan() plan}.
   * 
   * @throws NullPointerException if {@code selfSupervised} is null
   */
  public <T extends SelfSupervisor> void supervise(T supervisable) {
    link(supervisable, supervisable.selfPlan());
  }

  /**
   * Returns a supervised instance of the {@code type}, with supervision being dictated by the
   * {@code supervisor} 's plan.
   * 
   * @throws NullPointerException if {@code type} or {@code supervisor} are null
   * @throws ProvisionException if there was a runtime failure while providing an instance
   */
  public <C, S extends Supervisor> C supervised(Class<C> type, S supervisor) {
    Assert.notNull(type, "type");
    Assert.notNull(supervisor, "supervisor");
    C supervised = proxyFactory.proxyFor(type);
    link(supervisor, supervised);
    return supervised;
  }

  /**
   * Returns a supervised instance of the {@code supervisableType}, with supervision being dictated
   * by the {@code supervisableType}'s plan.
   * 
   * @throws NullPointerException if {@code type} is null
   * @throws ProvisionException if there was a runtime failure while providing an instance
   */
  public <T extends SelfSupervisor> T supervised(Class<T> supervisableType) {
    Assert.notNull(supervisableType, "supervisableType");
    T supervised = proxyFactory.proxyFor(supervisableType);
    link(supervised, supervised.selfPlan());
    return supervised;
  }

  /**
   * Returns a supervised instance of the type, with supervision being dictated by the {@code plan}.
   * 
   * @throws NullPointerException if {@code supervisedType} or {@code plan} are null
   * @throws ProvisionException if there was a runtime failure while providing an instance
   */
  public <T> T supervised(Class<T> type, Plan plan) {
    Assert.notNull(type, "type");
    Assert.notNull(plan, "plan");
    T supervised = proxyFactory.proxyFor(type);
    link(supervised, plan);
    return supervised;
  }

  /**
   * Returns a supervised instance of the type, with supervision being dictated by the
   * {@code planMaker}'s plan.
   * 
   * @throws NullPointerException if {@code supervisedType} or {@code planMaker} are null
   * @throws ProvisionException if there was a runtime failure while providing an instance
   */
  public <T> T supervised(Class<T> supervisedType, PlanMaker planMaker) {
    Assert.notNull(planMaker, "planMaker");
    return supervised(supervisedType, planMaker.make());
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
}
