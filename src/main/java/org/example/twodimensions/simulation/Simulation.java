package org.example.twodimensions.simulation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import org.example.sound.AudioData;
import org.example.sound.AudioFile;
import org.example.sound.SoundRecorder;

public class Simulation {

  static final int PARTICLES_X = 250; // Amount of particles along X axis
  static final int PARTICLES_Y = 2; // Amount of particles along Y axis
  static final double SPRING_LENGTH = 0.001;
  static final double WEIGHT = 0.01;
  static final double K = 1000000; // Spring constant
  static final double TIME_STEP = 1.0/8000; // Time between simulation steps [s]
  static final double DURATION = 20; // Duration of the simulation [s]
  static final long SIMULATION_STEPS = (long) (DURATION / TIME_STEP); // Calculated number of steps
  static final int SNAPSHOTS = 600; // Number of snapshots
  static final int RECORDER_DIST = 30;
  static final Particle[][] PARTICLES = new Particle[PARTICLES_X][PARTICLES_Y];
  static final String FILE_PATH = "src/main/resources/file.txt";
  static final BufferedWriter writer;
  static final boolean PULSE = false;
  static final double beatsPerSecond = 82.41;
  static final double AMPLITUDE = 0.001;
  static long lastUpdateIndex = 0;
  static AudioFile audioFile = new AudioFile("src/main/resources/Iv4n T3a - Bleeding Grasshopper.wav");
  static AudioData audioData = audioFile.extractAudioData();
  static SoundRecorder soundRecorder = new SoundRecorder("src/main/resources/outputAudio.wav");

  static {
    try {
      writer = new BufferedWriter(new FileWriter(FILE_PATH));
      writer.write(DURATION / SNAPSHOTS + "\n");
      writer.write(PARTICLES_X + "\n");
      writer.write(SNAPSHOTS + "\n");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  static final double[][][] snapshotArray = new double[SNAPSHOTS][PARTICLES_X][PARTICLES_Y];

  static boolean PRINT_PERCENTAGE = true;

  private static class Particle {
    final double startX, startY;
    double x, y;
    double vx = 0, vy = 0;
    double ax = 0, ay = 0;
    final double mass;
    boolean isLocked = false;

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
      if (!isLocked) {
        vx += ax * dt;
        vy += ay * dt;
        x += vx * dt;
        y += vy * dt;
      }
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
        boolean isFixed = isFixed(i, j);
        PARTICLES[i][j] = new Particle(i * SPRING_LENGTH, j * SPRING_LENGTH, WEIGHT);

        if (isFixed) {
          PARTICLES[i][j].vx = PARTICLES[i][j].vy = 0;
        }
      }
    }

//    PARTICLES[PARTICLES_X / 2][PARTICLES_Y / 2].vx -= 1;
//    PARTICLES[PARTICLES_X / 2][PARTICLES_Y / 2].vy -= 1;
//    dirtyParticlesConfig();
//    interferenceConfig();
//    lockedBorderConfig();
//    oneWayWaveConfig();
  }

  private static void dirtyParticlesConfig() {
    int separation = PARTICLES_X / 8;
    for (int i = 0; i < PARTICLES_X; i++) {
      for (int j = 0; j < PARTICLES_Y; j++) {
        if (separation * 6 < i && i < separation * 7 && separation * 6 < j && j < separation * 7) {
          PARTICLES[i][j].isLocked = true;
        }
      }
    }
    PARTICLES[1][1].vx -= 1;
    PARTICLES[1][1].vy -= 1;
  }

  private static void lockedBorderConfig() {
    for (int i = 0; i < PARTICLES_X; i++) {
      PARTICLES[i][0].isLocked = true;
      PARTICLES[i][PARTICLES_Y - 1].isLocked = true;
    }
    for (int i = 0; i < PARTICLES_Y; i++) {
      PARTICLES[0][i].isLocked = true;
      PARTICLES[PARTICLES_X - 1][i].isLocked = true;
    }
  }

  private static void oneWayWaveConfig()  {
    PARTICLES[PARTICLES_X / 2][PARTICLES_Y / 2].vx += 0.01;
  }

  private static void interferenceConfig() {
    int separationX = PARTICLES_X / 12;
    int separationY = PARTICLES_Y / 2;
    PARTICLES[separationX][separationY - 3].vx += 1;
    PARTICLES[separationX][separationY - 3].vy += 1;
    PARTICLES[separationX][separationY + 3].vx += 1;
    PARTICLES[separationX][separationY + 3].vy += 1;
  }

  private static void pulse(long stepIndex) {
    PARTICLES[1][1].isLocked = true;

    int xIndex = PARTICLES_X / 2;
    int yIndex = PARTICLES_Y / 2;
    double timePassed = (stepIndex - lastUpdateIndex) * TIME_STEP;

    double offset = AMPLITUDE * audioData.data()[(int)stepIndex];
    for (int i = 0; i < PARTICLES_Y; i++) {
      PARTICLES[xIndex][i].x = SPRING_LENGTH * xIndex + SPRING_LENGTH * offset;
      PARTICLES[xIndex][i].y = SPRING_LENGTH * i + 3 * SPRING_LENGTH * offset;
    }

//    if (PULSE && timePassed > 1.0 / beatsPerSecond) {
//      System.out.println("Pulse");
//      lastUpdateIndex = stepIndex;
//      PARTICLES[PARTICLES_X / 2][PARTICLES_Y / 2].vx -= 0.2;
//      PARTICLES[PARTICLES_X / 2][PARTICLES_Y / 2].vy -= 0.2;
//      for (int i = -10; i <= 10; i++) {
//        PARTICLES[xIndex][yIndex + i].x = SPRING_LENGTH * xIndex + 4 * SPRING_LENGTH * AMPLITUDE;
//      }
//      PARTICLES[xIndex][yIndex].y = SPRING_LENGTH * yIndex + 2 * SPRING_LENGTH / 5;
//    }
//    else {
//      for (int i = -10; i <= 10; i++) {
//        PARTICLES[xIndex][yIndex + i].x = SPRING_LENGTH * xIndex;
//      }
//      PARTICLES[xIndex][yIndex].y = SPRING_LENGTH * yIndex;
//    }
  }

  static void recordSound(int x, int y) {
    soundRecorder.recordValue(PARTICLES[x][y].x);
  }

  public static void writeAudioAndPlay() {
    soundRecorder.writeData(audioData.sampleRate(), audioData.byteDepth());
    soundRecorder.playData();
  }

  private static void simulateStep(long step) {
    for (int i = 0; i < PARTICLES_X; i++) {
      for (int j = 0; j < PARTICLES_Y; j++) {

        applyNeighborForces(i, j);
      }
    }

    for (int i = 0; i < PARTICLES_X; i++) {
      for (int j = 0; j < PARTICLES_Y; j++) {
        PARTICLES[i][j].update(TIME_STEP);
      }
    }

    if (step % (SIMULATION_STEPS / SNAPSHOTS) == 0) {
      if (PRINT_PERCENTAGE) {
        printPercent(step);
      }
      int snapshotNumber = (int) (step / (SIMULATION_STEPS / SNAPSHOTS));

//      setHeatMap(snapshotNumber);
    }

    pulse(step);
    recordSound(PARTICLES_X / 2 + RECORDER_DIST, PARTICLES_Y / 2 );
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
    double force = K * (distance - SPRING_LENGTH);

    double fx = force * dx / distance;
    double fy = force * dy / distance;

    p1.applyForce(fx, fy);
  }

  private static void setHeatMap(int snapshotNumber) {
    for (int i = 0; i < PARTICLES_X; i++) {
      for (int j = 0; j < PARTICLES_Y; j++) {
        snapshotArray[snapshotNumber][i][j] = Simulation.PARTICLES[i][j].getDelta();
        try {
          writer.write(snapshotNumber + " " + i + " " + j + " " + Simulation.PARTICLES[i][j].getDelta() + '\n');
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  private static boolean isFixed(int i, int j) {
    return (i == 0 && j == 0) || (i == PARTICLES_X - 1 && j == 0) ||
        (i == 0 && j == PARTICLES_Y - 1) || (i == PARTICLES_X - 1 && j == PARTICLES_Y - 1);
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
