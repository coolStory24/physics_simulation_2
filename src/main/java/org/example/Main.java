package org.example;

import org.example.visualisation.VisualisationFrame;

public class Main {

  static final int PARTICLES_X = 100;
  static final int PARTICLES_Y = 100;
  static final double K = 1.0;
  static final double DAMPING = 1;
  static final double TIME_STEP = 1.0E-1;
  static final long STEPS = 20_000L;
  static final int SNAPSHOTS = 1_000;

  static final double[][][] snapshotArray = new double[SNAPSHOTS][PARTICLES_X][PARTICLES_Y];

  private static class Particle {
    final double startX, startY;
    double x, y;
    double vx = 0, vy = 0;
    double ax = 0, ay = 0;
    final double mass;

    public Particle(double x, double y, double mass) {
      this.x = x;
      this.y = y;
      this.startX = x;
      this.startY = y;
      this.mass = mass;
    }

    public double getDelta() {
      return Math.sqrt(Math.pow(startX - x, 2) + Math.pow(startY - y, 2));
    }

    public void update(double dt) {
      vx += ax * dt;
      vy += ay * dt;
      vx *= DAMPING;
      vy *= DAMPING;
      x += vx * dt;
      y += vy * dt;
      ax = 0;
      ay = 0;
    }

    public void applyForce(double fx, double fy) {
      ax += fx / mass;
      ay += fy / mass;
    }
  }

  public static void main(String[] args) {
    Particle[][] particles = new Particle[PARTICLES_X][PARTICLES_Y];

    for (int i = 0; i < PARTICLES_X; i++) {
      for (int j = 0; j < PARTICLES_Y; j++) {
        boolean isFixed = (i == 0 && j == 0) || (i == PARTICLES_X - 1 && j == 0) ||
            (i == 0 && j == PARTICLES_Y - 1) || (i == PARTICLES_X - 1 && j == PARTICLES_Y - 1);
        particles[i][j] = new Particle(i, j, 1.0); // Масса = 1.0 для всех грузиков

        if (isFixed) {
          particles[i][j].vx = particles[i][j].vy = 0;
        }
      }
    }

    particles[0][0].vx -= 1;
    particles[0][0].vy -= 1;

    for (long step = 0; step < STEPS; step++) {
      for (int i = 0; i < PARTICLES_X; i++) {
        for (int j = 0; j < PARTICLES_Y; j++) {
          if ((i == 0 && j == 0) || (i == 0 && j == PARTICLES_Y - 1) ||
              (i == PARTICLES_X - 1 && j == 0) || (i == PARTICLES_X - 1 && j == PARTICLES_Y - 1)) {
            continue;
          }

          applyNeighborForces(particles, i, j);
        }
      }

      for (int i = 0; i < PARTICLES_X; i++) {
        for (int j = 0; j < PARTICLES_Y; j++) {
          particles[i][j].update(TIME_STEP);
        }
      }

      if (step % (STEPS / SNAPSHOTS) == 0) {
        System.out.printf("%.2f%%\n", (100.0 * step / STEPS));
        int snapshotNumber = (int) (step / (STEPS / SNAPSHOTS));

        setHeatMap(particles, snapshotNumber);
      }
    }

    var frame = new VisualisationFrame(snapshotArray);
  }

  private static void applyNeighborForces(Particle[][] particles, int i, int j) {
    Particle p = particles[i][j];

    if (i > 0) applySpringForce(p, particles[i - 1][j]);
    if (i < PARTICLES_X - 1) applySpringForce(p, particles[i + 1][j]);
    if (j > 0) applySpringForce(p, particles[i][j - 1]);
    if (j < PARTICLES_Y - 1) applySpringForce(p, particles[i][j + 1]);
  }

  private static void applySpringForce(Particle p1, Particle p2) {
    double dx = p2.x - p1.x;
    double dy = p2.y - p1.y;
    double distance = Math.sqrt(dx * dx + dy * dy);
    double force = K * (distance - 1.0);

    double fx = force * dx / distance;
    double fy = force * dy / distance;

    p1.applyForce(fx, fy);
  }

  private static void setHeatMap(Particle[][] particles, int snapshotNumber) {
    for (int i = 0; i < PARTICLES_X; i++) {
      for (int j = 0; j < PARTICLES_Y; j++) {
        snapshotArray[snapshotNumber][i][j] = particles[i][j].getDelta();
      }
    }
  }

  private static void printHeatMap(double[][] heatMap) {
    for (int i = 0; i < heatMap.length; i++) {
      for (int j = 0; j < heatMap[i].length; j++) {
        System.out.printf("%.2f ", heatMap[i][j]);
      }
    }
  }
}
