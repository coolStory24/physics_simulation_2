package org.example.sound;

import java.util.ArrayList;

public class SoundRecorder {
  private final AudioFile audioFile;
  private final ArrayList<Double> data;

  public SoundRecorder(String filePath) {
    this.audioFile = new AudioFile(filePath);
    this.data = new ArrayList<>();
  }

  public void recordValue(double value) {
    data.add(value);
  }

  public void writeData(float sampleRate, int byteDepth) {
    double maxPulse = data.stream().max(Double::compare).orElse(0.0);
    double minPulse = data.stream().min(Double::compare).orElse(0.0);
    audioFile.writeAudioData(
        new AudioData(data.stream().mapToDouble(d -> 2 * ((d - minPulse) / (maxPulse - minPulse)) - 1).toArray(), sampleRate, byteDepth)
    );
  }

  public void playData() {
    audioFile.play();
  }

}
