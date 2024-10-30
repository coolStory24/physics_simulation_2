package org.example.twodimensions.visualisation;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;

public class HeatMapVisualisation extends JPanel implements ActionListener, KeyListener {

  private AtomicInteger currFramesPerSecond = new AtomicInteger(25);
  private final double[][][] DATA;
  private Timer timer;

  private boolean isRunning = false;

  private double minValue = Double.POSITIVE_INFINITY;
  private double maxValue = Double.NEGATIVE_INFINITY;

  private final int FIELD_WIDTH;
  private final int FIELD_HEIGHT;
  private final int PADDING;
  private final int CELL_SIZE;
  private final int HEIGHT;
  private final int WIDTH;
  private final int STEPS;

  private final double SECONDS_BETWEEN_STEPS;

  private final Font TITLE_FONT = new Font("Serif", Font.BOLD, 24);
  private final Font STANDARD_FONT = new Font("Serif", Font.PLAIN, 18);
  private final int LINE_HEIGHT = 24;

  private final AtomicInteger currentFrame = new AtomicInteger(0);

  public HeatMapVisualisation(double secondsBetweenSteps, double[][][] data, int width, int height,
      int padding) {
    this.SECONDS_BETWEEN_STEPS = secondsBetweenSteps;
    this.DATA = data;
    this.FIELD_WIDTH = width;
    this.FIELD_HEIGHT = height;
    this.PADDING = padding;
    this.STEPS = data.length;
    this.WIDTH = data[0].length;
    this.HEIGHT = data[0].length;
    this.CELL_SIZE = (FIELD_HEIGHT - PADDING * 2) / HEIGHT;

    for (double[][] datum : data) {
      for (int j = 0; j < data[0].length; j++) {
        for (int k = 0; k < data[0][0].length; k++) {
          minValue = Math.min(minValue, datum[j][k]);
          maxValue = Math.max(maxValue, datum[j][k]);
        }
      }
    }

    setFocusable(true);

    addKeyListener(this);
    timer = new Timer(1000 / currFramesPerSecond.intValue(), this);
    timer.start();
    isRunning = true;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    paintBorders(g);
    int currFrame = currentFrame.get();
    if (currFrame < STEPS) {
      paintMap(DATA[currFrame], g);
    } else {
      isRunning = false;
      currentFrame.set(STEPS - 1);
    }

    paintInfo(g);
  }

  private void paintBorders(Graphics g) {
    g.setColor(Color.BLACK);
    g.drawRect(PADDING - 1, PADDING - 1, (FIELD_HEIGHT - PADDING * 2) + 1,
        (FIELD_HEIGHT - PADDING * 2) + 1);

    if (isRunning) {
      currentFrame.getAndIncrement();
    }
  }

  private void paintInfo(Graphics g) {
    g.setColor(Color.BLACK);
    g.setFont(TITLE_FONT);
    g.drawString("Current simulation params", FIELD_HEIGHT, PADDING);
    g.setFont(STANDARD_FONT);
    g.drawString(String.format("FPS: %d", currFramesPerSecond.intValue()), FIELD_HEIGHT,
        PADDING + LINE_HEIGHT);

    String runningMessage;

    if (currentFrame.intValue() < STEPS - 1) {
      runningMessage = isRunning ? "Running..." : "Paused, press [SPACE] to continue";
    } else {
      runningMessage = "Finished";
    }

    g.drawString(runningMessage, FIELD_HEIGHT, PADDING + LINE_HEIGHT * 2);
    g.drawRect(FIELD_HEIGHT, PADDING + LINE_HEIGHT * 3, 200, 10);
    g.setColor(Color.BLUE);
    g.fillRect(FIELD_HEIGHT, PADDING + LINE_HEIGHT * 3,
        (200 * currentFrame.intValue()) / (STEPS - 1), 10);

    g.setColor(Color.BLACK);
    g.drawString(String.format("Time scale: %.3f",
            SECONDS_BETWEEN_STEPS * currFramesPerSecond.doubleValue()), FIELD_HEIGHT,
        PADDING + LINE_HEIGHT * 5);
  }

  private void paintMap(double[][] heatMap, Graphics g) {

    for (int y = 0; y < HEIGHT; y++) {
      for (int x = 0; x < WIDTH; x++) {

        var color = HeatMapColor.getHeatMapColor(heatMap[x][y], minValue, maxValue);
        g.setColor(color);

        g.fillRect(CELL_SIZE * x + PADDING, CELL_SIZE * y + PADDING, CELL_SIZE, CELL_SIZE);
      }
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    repaint();
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void keyPressed(KeyEvent e) {
    switch (e.getKeyCode()) {
      case KeyEvent.VK_SPACE -> isRunning = !isRunning;
      case KeyEvent.VK_RIGHT ->
          currentFrame.set(Math.min(currentFrame.intValue() + STEPS / 20, STEPS - 1));
      case KeyEvent.VK_LEFT -> currentFrame.set(Math.max(currentFrame.intValue() - STEPS / 20, 0));
      case KeyEvent.VK_UP -> {
        timer.setDelay(1_000 / currFramesPerSecond.incrementAndGet());
      }
      case KeyEvent.VK_DOWN -> {
        int val = currFramesPerSecond.get();
        if (val > 1) {
          timer.setDelay(1_000 / currFramesPerSecond.compareAndExchange(val, val - 1));
        }
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
  }
}
