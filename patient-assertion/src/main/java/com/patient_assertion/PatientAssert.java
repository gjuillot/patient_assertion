package com.patient_assertion;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.core.ConditionFactory;
import com.jayway.awaitility.core.ConditionTimeoutException;

public abstract class PatientAssert<A extends PatientAssert<A>> {
  
  private static final long DEFAULT_TIMEOUT = 10;
  
  protected ConditionFactory conditionFactory;
  
  /**
   * Use this if you already have a ConditionFactory (for example in second class in a assertions chain).
   */
  protected PatientAssert(ConditionFactory conditionFactory) {
    this.conditionFactory = conditionFactory;
  }
  
  /**
   * Use this if you want a specific timeout.
   */
  protected PatientAssert(long timeOut, TimeUnit timeoutUnit) {
    this(Awaitility.await().atMost(timeOut, timeoutUnit).ignoreExceptions());
  }
  
  /**
   * Constructor with default timeout of 10 SECONDS.
   */
  protected PatientAssert() {
    this(DEFAULT_TIMEOUT, SECONDS);
  }
  
  public A before(long timeOut, TimeUnit timeUnit) {
    conditionFactory = conditionFactory.atMost(timeOut, timeUnit);
    return (A) this;
  }
  
  public final A withPollInterval(long pollInterval, TimeUnit unit) {
    conditionFactory = conditionFactory.pollInterval(pollInterval, unit);
    return (A) this;
  }
  
  public final A withPollDelay(long pollDelay, TimeUnit unit) {
    conditionFactory = conditionFactory.pollDelay(pollDelay, unit);
    return (A) this;
  }

  protected void waitUntil(Callable<Boolean> conditionEvaluator, String assertionErrorMessage) {
    try {
      conditionFactory.until(conditionEvaluator);
    } catch (ConditionTimeoutException e) {
      throw new AssertionError(assertionErrorMessage, e);
    }
  }

  protected <T> T waitUntil(Supplier<T> supplier, Predicate<? super T> predicate, Function<T, String> assertionErrorMessage) {
    AtomicReference<T> storedValue = new AtomicReference<>();
    try {
      return waitUntil(() -> storedValue.updateAndGet(r -> supplier.get()), predicate);
    } catch (ConditionTimeoutException e) {
      throw new AssertionError(assertionErrorMessage.apply(storedValue.get()), e);
    }
  }

  private <T> T waitUntil(Callable<T> supplier, Predicate<? super T> predicate) {
    return conditionFactory.until(supplier::call, matcherFor(predicate));
  }

  protected void waitUntil(Runnable supplier, Function<Exception, String> assertionErrorMessage) {
    try {
      waitUntil(supplier);
    } catch (ConditionTimeoutException e) {
      throw new AssertionError(assertionErrorMessage.apply(e), e);
    }
  }

  private void waitUntil(Runnable supplier) {
    conditionFactory.until(supplier);
  }
  
  private static <T extends Object> Matcher<T> matcherFor(final Predicate<T> predicate) {
    return new BaseMatcher<T>() {

      @Override
      public boolean matches(Object o) {
        return o != null && predicate.test((T) o);
      }

      @Override
      public void describeTo(Description description) {
      }
    };
  }
  
}
