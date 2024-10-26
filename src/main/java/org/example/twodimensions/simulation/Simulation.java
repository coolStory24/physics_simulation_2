package org.example.twodimensions.simulation;

public class Simulation {

  static final int PARTICLES_X = 100; // Amount of particles along X axis
  static final int PARTICLES_Y = 100; // Amount of particles along Y axis
  static final double K = 0.0001; // Spring constant
  static final double TIME_STEP = 1.0E-1; // Time between simulation steps [s]
  static final double DURATION = 2.0E3; // Duration of the simulation [s]
  static final long SIMULATION_STEPS = (long) (DURATION / TIME_STEP); // Calculated number of steps
  static final int SNAPSHOTS = 1_000; // Number of snapshots
  static final Particle[][] PARTICLES = new Particle[PARTICLES_X][PARTICLES_Y];

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

  private static void init() {
    for (int i = 0; i < PARTICLES_X; i++) {
      for (int j = 0; j < PARTICLES_Y; j++) {
        boolean isFixed = (i == 0 && j == 0) || (i == PARTICLES_X - 1 && j == 0) ||
            (i == 0 && j == PARTICLES_Y - 1) || (i == PARTICLES_X - 1 && j == PARTICLES_Y - 1);
        PARTICLES[i][j] = new Particle(i, j, 1);

        if (isFixed) {
          PARTICLES[i][j].vx = PARTICLES[i][j].vy = 0;
        }
      }
    }

    PARTICLES[0][0].vx -= 1;
    PARTICLES[0][0].vy -= 1;
  }

  private static void simulateStep(long step) {
    for (int i = 0; i < PARTICLES_X; i++) {
      for (int j = 0; j < PARTICLES_Y; j++) {
        if ((i == 0 && j == 0) || (i == 0 && j == PARTICLES_Y - 1) ||
            (i == PARTICLES_X - 1 && j == 0) || (i == PARTICLES_X - 1 && j == PARTICLES_Y - 1)) {
          continue;
        }

        applyNeighborForces(i, j);
      }
    }

    for (int i = 0; i < PARTICLES_X; i++) {
      for (int j = 0; j < PARTICLES_Y; j++) {
        PARTICLES[i][j].update(TIME_STEP);
      }
    }

    if (step % (SIMULATION_STEPS / SNAPSHOTS) == 0) {
      printPercent(step);
      int snapshotNumber = (int) (step / (SIMULATION_STEPS / SNAPSHOTS));

      setHeatMap(snapshotNumber);
    }
  }

  public static double[][][] simulate() {
    init();

    for (long step = 0; step < SIMULATION_STEPS; step++) {
      simulateStep(step);
    }

    return snapshotArray;
  }

  private static void applyNeighborForces(int i, int j) {
    Particle p = Simulation.PARTICLES[i][j];

    if (i > 0) applySpringForce(p, Simulation.PARTICLES[i - 1][j]);
    if (i < PARTICLES_X - 1) applySpringForce(p, Simulation.PARTICLES[i + 1][j]);
    if (j > 0) applySpringForce(p, Simulation.PARTICLES[i][j - 1]);
    if (j < PARTICLES_Y - 1) applySpringForce(p, Simulation.PARTICLES[i][j + 1]);
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

  private static void setHeatMap(int snapshotNumber) {
    for (int i = 0; i < PARTICLES_X; i++) {
      for (int j = 0; j < PARTICLES_Y; j++) {
        snapshotArray[snapshotNumber][i][j] = Simulation.PARTICLES[i][j].getDelta();
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

  private static void printPercent(long step){
    String formatted = String.format("%.2f %%", (100.0 * step / SIMULATION_STEPS));
    System.out.println(formatted);
  }
}
