package org.example.simulation;

import java.util.Arrays;
import org.example.Visualisation;

public class WaveSimulation1D {

  private static class Particle {

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

    @Override
    public String toString() {
      return "Particle{" +
          "startPosition=" + startPosition +
          ", position=" + position +
          ", velocity=" + velocity +
          ", acceleration=" + acceleration +
          ", mass=" + mass +
          '}';
    }

    public double getDelta() {
      return startPosition - position;
    }
  }

  static final int PARTICLES = 100;
  static final double K = 1;
  static final double DAMPING = 1;
  static final double L = 1E-4;
  static final long STEPS = 20_000L;
  static final int SNAPSHOTS = 10_000;
  static final double TIME_STEP = 1.0E-2;

  public static void main(String[] args) {
    Particle[] particles = new Particle[PARTICLES];
    for (int i = 0; i < PARTICLES; i++) {
      particles[i] = new Particle(i * L, 0, 1);
    }

    particles[0].position = -1.0E-3;

    double prevMax = 0.0;
    double prevSum = 0.0;

    var result = new double[SNAPSHOTS];

    for (long step = 0L; step < STEPS; step++) {
      simulateStep(particles);

//      System.out.println(particles[particles.length - 2].getDelta() * 1E8);

      prevMax = Math.max(prevMax, particles[particles.length - 2].getDelta() * 1E8);
      prevSum += particles[particles.length - 2].getDelta() * 1E8;

      if ((step) % (STEPS / SNAPSHOTS) == 0) {
        result[(int) (step / (STEPS / SNAPSHOTS))] = particles[particles.length - 2].getDelta();
        prevMax = 0.0;
        prevSum = 0.0;
      }

      if (step % (STEPS / SNAPSHOTS) == 0) {
        printPositions(particles);

      }
    }

    var visualisation = new Visualisation("GOOOG", result);
    visualisation.visualise();
  }

  private static void simulateStep(Particle[] particles) {
    for (int i = 1; i < particles.length - 1; i++) {
      double forceLeft = -K * (particles[i].position - particles[i - 1].position +L);
      double forceRight = -K * (particles[i].position - particles[i + 1].position - L);
      double totalForce = forceLeft + forceRight;
      particles[i].acceleration = totalForce / particles[i].mass;
    }

    for (int i = 1; i < particles.length - 1; i++) {
      particles[i].velocity += particles[i].acceleration * TIME_STEP;
      particles[i].velocity *= DAMPING;
      particles[i].position += particles[i].velocity * TIME_STEP;
    }
  }

  private static void printPositions(Particle[] particles) {
    System.out.println(
        Arrays.toString(Arrays.stream(particles).mapToDouble(Particle::getDelta).toArray()));
  }
}

