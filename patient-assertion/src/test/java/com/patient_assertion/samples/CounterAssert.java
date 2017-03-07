package com.patient_assertion.samples;

import com.patient_assertion.PatientAssert;

public class CounterAssert extends PatientAssert<CounterAssert> {
  
  private final SlowCounter counter;
  
  private CounterAssert(SlowCounter counter) {
    this.counter = counter;
  }
  
  public static CounterAssert assertThat(SlowCounter counter) {
    return new CounterAssert(counter);
  }

  public void reaches(int expectedValue) {
    waitUntil(
        counter::getValue,
        actualValue -> actualValue == expectedValue,
        actualValue -> "Counter did not reached value " + expectedValue + " but reached value " + actualValue);
  }
}
