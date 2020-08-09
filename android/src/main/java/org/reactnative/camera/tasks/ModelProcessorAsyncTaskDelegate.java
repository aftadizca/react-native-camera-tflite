package org.reactnative.camera.tasks;

public interface ModelProcessorAsyncTaskDelegate {
  void onModelProcessed(float[][] data, int sourceWidth, int sourceHeight, int sourceRotation);
  void onModelProcessorTaskCompleted();
}
