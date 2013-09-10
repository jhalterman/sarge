package net.jodah.sarge;

import net.jodah.sarge.internal.ProxyFactory;
import net.jodah.sarge.internal.SupervisedInterceptor;
import net.jodah.sarge.internal.SupervisionRegistry;
import net.jodah.sarge.internal.util.Assert;

import org.aopalliance.intercept.MethodInterceptor;

import com.google.inject.ProvisionException;

/**
 * Creates supervised objects, with failures being handled according to a {@link Plan}.
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
   * Returns an instance of the {@code type} that is capable of being supervised by calling one of
   * the supervise methods.
   * 
   * @throws NullPointerException if {@code type} is null
   */
  public <T> T supervisable(Class<T> type) {
    Assert.notNull(type, "type");
    return proxyFactory.proxyFor(type);
  }

  /**
   * Supervises the {@code supervisable} object according to the {@code plan}.
   * 
   * @throws NullPointerException if {@code supervisable} or {@code plan} are null
   * @throws IllegalArgumentException if {@code supervised} is already supervised
   */
  public void supervise(Object supervisable, Plan plan) {
    Assert.notNull(supervisable, "supervisable");
    Assert.notNull(plan, "plan");
    registry.supervise(supervisable, plan);
  }

  /**
   * Supervises the {@code supervisable} object according to the {@code planMaker}'s
   * {@link PlanMaker#make() plan}.
   * 
   * @throws NullPointerException if {@code supervisable} or {@code planMaker} are null
   * @throws IllegalArgumentException if {@code supervised} is already supervised
   */
  public void supervise(Object supervisable, PlanMaker planMaker) {
    Assert.notNull(supervisable, "supervisable");
    Assert.notNull(planMaker, "planMaker");
    registry.supervise(supervisable, planMaker.make());
  }

  /**
   * Supervises the {@code selfSupervisable} object according to its
   * {@link SelfSupervisor#selfPlan() selfPlan}.
   * 
   * @throws NullPointerException if {@code selfSupervised} is null
   */
  public <T extends SelfSupervisor> void supervise(T selfSupervisable) {
    Assert.notNull(selfSupervisable, "selfSupervisable");
    supervise(selfSupervisable, selfSupervisable.selfPlan());
  }

  /**
   * Supervises the {@code supervisable} object with the {@code supervisor}'s
   * {@link Supervisor#plan() plan}, forming a parent-child supervision relationship between the
   * {@code supervisor} and {@code supervisable} where failures can be escalated.
   * 
   * <p>
   * Any self-supervision for the {@code supervisable} is overriden.
   * 
   * @throws NullPointerException if {@code supervised} or {@code supervisor} are null
   * @throws IllegalArgumentException if {@code supervisable} is already supervised
   */
  public <T extends Supervisor> void supervise(Object supervisable, T supervisor) {
    Assert.notNull(supervisable, "supervisable");
    Assert.notNull(supervisor, "supervisor");
    registry.supervise(supervisable, supervisor);
  }

  /**
   * Returns an instance of the {@code type} that is supervised by the {@code supervisor}'s
   * {@link Supervisor#plan() plan}, forming a parent-child supervision relationship between the
   * {@code supervisor} and the result where failures can be escalated.
   * 
   * @throws NullPointerException if {@code type} or {@code supervisor} are null
   * @throws ProvisionException if there was a runtime failure while providing an instance
   */
  public <C, S extends Supervisor> C supervised(Class<C> type, S supervisor) {
    Assert.notNull(type, "type");
    Assert.notNull(supervisor, "supervisor");
    C supervisable = proxyFactory.proxyFor(type);
    supervise(supervisable, supervisor);
    return supervisable;
  }

  /**
   * Returns an instance of the {@code selfSupervisable} that is supervised by the by the
   * {@code selfSupervisable}'s {@link SelfSupervisor#selfPlan() selfPlan}.
   * 
   * @throws NullPointerException if {@code selfSupervisable} is null
   * @throws ProvisionException if there was a runtime failure while providing an instance
   */
  public <T extends SelfSupervisor> T supervised(Class<T> selfSupervisable) {
    Assert.notNull(selfSupervisable, "selfSupervisable");
    T supervisable = proxyFactory.proxyFor(selfSupervisable);
    supervise(supervisable, supervisable.selfPlan());
    return supervisable;
  }

  /**
   * Returns an instance of the {@code type} that is supervised by the {@code plan}.
   * 
   * @throws NullPointerException if {@code supervisedType} or {@code plan} are null
   * @throws ProvisionException if there was a runtime failure while providing an instance
   */
  public <T> T supervised(Class<T> type, Plan plan) {
    Assert.notNull(type, "type");
    Assert.notNull(plan, "plan");
    T supervisable = proxyFactory.proxyFor(type);
    supervise(supervisable, plan);
    return supervisable;
  }

  /**
   * Returns an instance of the {@code type} that is supervised by the {@code planMaker}'s
   * {@link PlanMaker#make() plan}.
   * 
   * @throws NullPointerException if {@code type} or {@code planMaker} are null
   * @throws ProvisionException if there was a runtime failure while providing an instance
   */
  public <T> T supervised(Class<T> type, PlanMaker planMaker) {
    Assert.notNull(type, "type");
    Assert.notNull(planMaker, "planMaker");
    return supervised(type, planMaker.make());
  }

  /**
   * Unsupervises the {@code supervised} object.
   * 
   * @throws NullPointerException if {@code supervised} is null
   * @throws IllegalStateException if {@code supervised} is not supervised
   */
  public void unsupervise(Object supervised) {
    Assert.notNull(supervised, "supervised");
    registry.unsupervise(supervised);
  }
}
