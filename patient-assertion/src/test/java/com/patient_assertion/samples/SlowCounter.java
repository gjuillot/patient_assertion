package com.patient_assertion.samples;

public class SlowCounter {

  private int value = 0;
  
  public int getValue() {
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    return value++;
  }
}
