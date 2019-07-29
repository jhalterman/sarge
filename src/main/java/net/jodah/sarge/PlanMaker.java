package net.jodah.sarge;

import net.jodah.sarge.internal.util.Assert;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Makes a {@link Plan}.
 * 
 * @author Jonathan Halterman
 */
public class PlanMaker {
  private final Map<Class<? extends Throwable>, Directive> directives = new LinkedHashMap<Class<? extends Throwable>, Directive>();

  PlanMaker() {
  }

  /**
   * Makes a {@link Plan}.
   * 
   * @return newly made {@link Plan}
   */
  public Plan make() {
    return new Plan() {
      public Directive apply(Throwable cause) {
        for (Map.Entry<Class<? extends Throwable>, Directive> entry : directives.entrySet())
          if (entry.getKey()
                   .isAssignableFrom(cause.getClass()))
            return entry.getValue();
        return null;
      }
    };
  }

  /**
   * @throws NullPointerException if {@code causeType} is null
   */
  @SuppressWarnings("unchecked")
  public PlanMaker escalateOn(Class<? extends Throwable> causeType) {
    return addDirective(Directive.Escalate, causeType);
  }

  /**
   * @throws NullPointerException if {@code causeTypes} is null
   */
  public PlanMaker escalateOn(Class<? extends Throwable>... causeTypes) {
    return addDirective(Directive.Escalate, causeTypes);
  }

  /**
   * @throws NullPointerException if {@code causeType} is null
   */
  @SuppressWarnings("unchecked")
  public PlanMaker onFailure(Class<? extends Throwable> causeType, Directive directive) {
    return addDirective(directive, causeType);
  }

  @SuppressWarnings("unchecked")
  public PlanMaker resumeOn(Class<? extends Throwable> causeType) {
    return addDirective(Directive.Resume, causeType);
  }

  /**
   * @throws NullPointerException if {@code causeTypes} is null
   */
  public PlanMaker resumeOn(Class<? extends Throwable>... causeTypes) {
    return addDirective(Directive.Resume, causeTypes);
  }

  /**
   * @throws NullPointerException if {@code causeType} is null
   */
  @SuppressWarnings("unchecked")
  public PlanMaker rethrowOn(Class<? extends Throwable> causeType) {
    return addDirective(Directive.Rethrow, causeType);
  }

  /**
   * @throws NullPointerException if {@code causeTypes} is null
   */
  public PlanMaker rethrowOn(Class<? extends Throwable>... causeTypes) {
    return addDirective(Directive.Rethrow, causeTypes);
  }

  /**
   * Create a retry when a failure of {@code causeType} occurs, retrying up to {@code maxRetries}
   * times within the {@code retryWindow} with zero wait time between retries.
   * 
   * @throws NullPointerException if {@code causeType} is null
   */
  @SuppressWarnings("unchecked")
  public PlanMaker retryOn(Class<? extends Throwable> causeType, int maxRetries,
      Duration retryWindow) {
    return addDirective(Directive.Retry(maxRetries, retryWindow), causeType);
  }

  /**
   * Performs a retry when a failure of {@code causeType} occurs, retrying up to {@code maxRetries}
   * times within the {@code retryWindow}, backing off and waiting between each retry according to
   * the {@code backoffExponent} up to {@code maxRetryInterval}.
   * 
   * @throws NullPointerException if {@code causeType} is null
   */
  @SuppressWarnings("unchecked")
  public PlanMaker retryOn(Class<? extends Throwable> causeType, int maxRetries,
      Duration retryWindow, Duration initialRetryInterval, double backoffExponent,
      Duration maxRetryInterval) {
    return addDirective(Directive.Retry(maxRetries, retryWindow, initialRetryInterval,
        backoffExponent, maxRetryInterval), causeType);
  }

  /**
   * Performs a retry when a failure of {@code causeType} occurs, retrying up to {@code maxRetries}
   * times within the {@code retryWindow}, backing off and waiting between each retry up to
   * {@code maxRetryInterval}.
   * 
   * @throws NullPointerException if {@code causeType} is null
   */
  @SuppressWarnings("unchecked")
  public PlanMaker retryOn(Class<? extends Throwable> causeType, int maxRetries,
      Duration retryWindow, Duration initialRetryInterval, Duration maxRetryInterval) {
    return addDirective(
        Directive.Retry(maxRetries, retryWindow, initialRetryInterval, 2, maxRetryInterval),
        causeType);
  }

  /**
   * Perform a retry when a failure of any of the {@code causeTypes} occurs, retrying up to
   * {@code maxRetries} times within the {@code retryWindow} with zero wait time between retries.
   * 
   * @throws NullPointerException if {@code causeTypes} is null
   */
  public PlanMaker retryOn(Class<? extends Throwable>[] causeTypes, int maxRetries,
      Duration retryWindow) {
    return addDirective(Directive.Retry(maxRetries, retryWindow), causeTypes);
  }

  /**
   * Perform a retry when a failure of any of the {@code causeTypes} occurs, retrying up to
   * {@code maxRetries} times within the {@code retryWindow}, backing off and waiting between each
   * retry up to {@code maxRetryInterval}.
   * 
   * @throws NullPointerException if {@code causeType} is null
   */
  public PlanMaker retryOn(Class<? extends Throwable>[] causeTypes, int maxRetries,
      Duration retryWindow, Duration initialRetryInterval, Duration maxRetryInterval) {
    return addDirective(
        Directive.Retry(maxRetries, retryWindow, initialRetryInterval, 2, maxRetryInterval),
        causeTypes);
  }

  /**
   * @throws IllegalStateException if any of the {@code causeTypes} have already been added.
   */
  PlanMaker addDirective(Directive directive, Class<? extends Throwable>... causeTypes) {
    Assert.notNull(directive, "directive");
    Assert.notNull(causeTypes, "causeType");
    for (Class<? extends Throwable> causeType : causeTypes)
      Assert.state(directives.put(causeType, directive) == null,
          "A directive for " + causeType.getName() + " already exists");
    return this;
  }
}
