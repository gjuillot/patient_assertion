package com.patient_assertion.samples;

import static com.patient_assertion.samples.CounterAssert.assertThat;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.testng.annotations.Test;

public class CounterTest {
  
  @Test(timeOut = 11_000)
  public void default_timeout_should_be_taken_into_account() {
    assertThat(new SlowCounter()).reaches(15);
  }
  
  @Test(timeOut = 11_000, expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Counter did not reached value 20 but reached value 16")
  public void error_message_should_be_displayed() {
    assertThat(new SlowCounter()).reaches(20);
  }
  
  @Test(timeOut = 16_000)
  public void before_should_be_taken_into_account() {
    assertThat(new SlowCounter()).before(15, SECONDS).reaches(20);
  }
  
  @Test(timeOut = 11_000, expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "Counter did not reached value 2 but reached value 1")
  public void with_poll_interval_should_be_taken_into_account() {
    assertThat(new SlowCounter()).withPollInterval(4, SECONDS).reaches(2);
  }

}
