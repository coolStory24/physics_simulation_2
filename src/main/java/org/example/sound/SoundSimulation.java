package org.example.sound;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class SoundSimulation {
  private final String inputFilePath;
  private final String outputFilePath;
  private final int particlesAmount;
  private final double[][] particles;
  private final double k;
  private final double weight;
  private BufferedWriter outputWriter;

  public SoundSimulation(String inputFilePath, String outputFilePath, int particlesAmount, double k, double weight) {
    this.inputFilePath = inputFilePath;
    this.outputFilePath = outputFilePath;
    this.particlesAmount = particlesAmount;
    this.particles = new double[particlesAmount][3];
    this.k = k;
    this.weight = weight;
    try {
      this.outputWriter = new BufferedWriter(new FileWriter(outputFilePath));
    } catch (IOException e) {
      throw new RuntimeException("Failed to open file", e);
    }
  }

  public void run() {

  }

  public SoundSimulation(String inputFilePath, String outputFilePath) {
    this(inputFilePath, outputFilePath, 1000, 0.1, 0.01);
  }

  private double getPosition(int i) {
    return particles[i][0];
  }

  private void setPosition(int i, double position) {
    particles[i][0] = position;
  }

  private double getVelocity(int i) {
    return particles[i][1];
  }

  private void setVelocity(int i, double velocity) {
    particles[i][1] = velocity;
  }
}
