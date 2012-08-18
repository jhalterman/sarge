package org.jodah.sarge;

import org.jodah.sarge.util.Duration;

/**
 * Defines a strategy for supervising objects.
 * 
 * @author Jonathan Halterman
 */
public final class Plans {
  private Plans() {
  }

  /**
   * @throws NullPointerException if {@code causeType} is null
   */
  @SuppressWarnings("unchecked")
  public static PlanMaker escalateOn(Class<? extends Throwable> causeType) {
    return new PlanMaker().addDirective(Directive.Escalate, causeType);
  }

  /**
   * @throws NullPointerException if {@code causeTypes} is null
   */
  public static PlanMaker escalateOn(Class<? extends Throwable>... causeTypes) {
    return new PlanMaker().addDirective(Directive.Escalate, causeTypes);
  }

  /**
   * @throws NullPointerException if {@code causeType} is null
   */
  @SuppressWarnings("unchecked")
  public static PlanMaker onFailure(Class<? extends Throwable> causeType, Directive directive) {
    return new PlanMaker().addDirective(directive, causeType);
  }

  @SuppressWarnings("unchecked")
  public static PlanMaker resumeOn(Class<? extends Throwable> causeType) {
    return new PlanMaker().addDirective(Directive.Resume, causeType);
  }

  /**
   * @throws NullPointerException if {@code causeTypes} is null
   */
  public static PlanMaker resumeOn(Class<? extends Throwable>... causeTypes) {
    return new PlanMaker().addDirective(Directive.Resume, causeTypes);
  }

  /**
   * @throws NullPointerException if {@code causeType} is null
   */
  @SuppressWarnings("unchecked")
  public static PlanMaker rethrowOn(Class<? extends Throwable> causeType) {
    return new PlanMaker().addDirective(Directive.Rethrow, causeType);
  }

  /**
   * @throws NullPointerException if {@code causeTypes} is null
   */
  public static PlanMaker rethrowOn(Class<? extends Throwable>... causeTypes) {
    return new PlanMaker().addDirective(Directive.Rethrow, causeTypes);
  }

  /**
   * Create a retry when a failure of {@code causeType} occurs, retrying up to {@code maxRetries}
   * times within the {@code retryWindow} with zero wait time between retries.
   * 
   * @throws NullPointerException if {@code causeType} is null
   */
  @SuppressWarnings("unchecked")
  public static PlanMaker retryOn(Class<? extends Throwable> causeType, int maxRetries,
      Duration retryWindow) {
    return new PlanMaker().addDirective(Directive.Retry(maxRetries, retryWindow), causeType);
  }

  /**
   * Performs a retry when a failure of {@code causeType} occurs, retrying up to {@code maxRetries}
   * times within the {@code retryWindow}, backing off and waiting between each retry according to
   * the {@code backoffExponent} up to {@code maxRetryInterval}.
   * 
   * @throws NullPointerException if {@code causeType} is null
   */
  @SuppressWarnings("unchecked")
  public static PlanMaker retryOn(Class<? extends Throwable> causeType, int maxRetries,
      Duration retryWindow, Duration initialRetryInterval, double backoffExponent,
      Duration maxRetryInterval) {
    return new PlanMaker().addDirective(Directive.Retry(maxRetries, retryWindow,
        initialRetryInterval, backoffExponent, maxRetryInterval), causeType);
  }

  /**
   * Performs a retry when a failure of {@code causeType} occurs, retrying up to {@code maxRetries}
   * times within the {@code retryWindow}, backing off and waiting between each retry up to
   * {@code maxRetryInterval}.
   * 
   * @throws NullPointerException if {@code causeType} is null
   */
  @SuppressWarnings("unchecked")
  public static PlanMaker retryOn(Class<? extends Throwable> causeType, int maxRetries,
      Duration retryWindow, Duration initialRetryInterval, Duration maxRetryInterval) {
    return new PlanMaker().addDirective(
        Directive.Retry(maxRetries, retryWindow, initialRetryInterval, 2, maxRetryInterval),
        causeType);
  }

  /**
   * Perform a retry when a failure of any of the {@code causeTypes} occurs, retrying up to
   * {@code maxRetries} times within the {@code timeRange} with zero wait time between retries.
   * 
   * @throws NullPointerException if {@code causeTypes} is null
   */
  public static PlanMaker retryOn(Class<? extends Throwable>[] causeTypes, int maxRetries,
      Duration timeRange) {
    return new PlanMaker().addDirective(Directive.Retry(maxRetries, timeRange), causeTypes);
  }

  /**
   * Perform a retry when a failure of any of the {@code causeTypes} occurs, retrying up to
   * {@code maxRetries} times within the {@code retryWindow}, backing off and waiting between each
   * retry up to {@code maxRetryInterval}.
   * 
   * @throws NullPointerException if {@code causeType} is null
   */
  public static PlanMaker retryOn(Class<? extends Throwable>[] causeTypes, int maxRetries,
      Duration retryWindow, Duration initialRetryInterval, Duration maxRetryInterval) {
    return new PlanMaker().addDirective(
        Directive.Retry(maxRetries, retryWindow, initialRetryInterval, 2, maxRetryInterval),
        causeTypes);
  }
}
