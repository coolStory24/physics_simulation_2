package org.example.simulation;

import java.util.Arrays;

public class WaveSimulation1D {
  static class Particle {
    final double startPosition;
    double position;
    double velocity;
    double acceleration;
    final double mass;

    public Particle(double position, double velocity, double mass) {
      this.position = position;
      this.startPosition = position;
      this.velocity = velocity;
      this.acceleration = 0;
      this.mass = mass;
    }

    public double getDelta() {
      return Math.abs(startPosition - position);
    }
  }

  static final int PARTICLES = 100;
  static final double K = 1;
  static final double DAMPING = 0.99999;
  static final long STEPS = 1_000_000L;
  static final double TIME_STEP = 1.0E-1;

  public static void main(String[] args) {
    Particle[] particles = new Particle[PARTICLES];
    for (int i = 0; i < PARTICLES; i++) {
      particles[i] = new Particle(i * 1.0E-4, 0, 1);
    }

    particles[0].position = -1.0E-3;

    for (int step = 0; step < STEPS; step++) {
      simulateStep(particles);

      if (step % (STEPS / 10000) == 0) {
        printPositions(particles);
      }
    }
  }

  public static void simulateStep(Particle[] particles) {
    for (int i = 1; i < particles.length - 1; i++) {
      double forceLeft = -K * (particles[i].position - particles[i - 1].position);
      double forceRight = -K * (particles[i].position - particles[i + 1].position);
      double totalForce = forceLeft + forceRight;
      particles[i].acceleration = totalForce / particles[i].mass;
    }

    for (int i = 1; i < particles.length - 1; i++) {
      particles[i].velocity += particles[i].acceleration * TIME_STEP;
      particles[i].velocity *= DAMPING;
      particles[i].position += particles[i].velocity * TIME_STEP;
    }
  }

  public static void printPositions(Particle[] particles) {
    System.out.println(Arrays.toString(Arrays.stream(particles).mapToDouble(Particle::getDelta).toArray()));
  }
}

