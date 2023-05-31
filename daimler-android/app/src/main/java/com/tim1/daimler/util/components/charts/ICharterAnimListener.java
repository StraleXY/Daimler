package com.tim1.daimler.util.components.charts;

public interface ICharterAnimListener {
  void onAnimStart();

  void onAnimFinish();

  void onAnimCancel();

  void onAnimProgress(float progress);
}
