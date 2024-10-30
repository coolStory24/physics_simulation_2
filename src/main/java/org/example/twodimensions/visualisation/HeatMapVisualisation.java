package org.example.twodimensions.visualisation;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;

public class HeatMapVisualisation extends JPanel implements ActionListener {

  private int currFramesPerSecond = 20;
  private final double[][][] DATA;

  private double minValue = Double.POSITIVE_INFINITY;
  private double maxValue = Double.NEGATIVE_INFINITY;

  private final int FIELD_WIDTH;
  private final int FIELD_HEIGHT;
  private final int PADDING;
  private final int CELL_SIZE;
  private final int HEIGHT;
  private final int WIDTH;
  private final int STEPS;

  private final Font TITLE_FONT = new Font("Serif", Font.BOLD, 24);
  private final Font STANDARD_FONT = new Font("Serif", Font.PLAIN, 18);
  private final int LINE_HEIGHT = 24;

  private final AtomicInteger currentFrame = new AtomicInteger(0);

  public HeatMapVisualisation(double[][][] data, int width, int height, int padding) {
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

    Timer timer = new Timer(1000 / currFramesPerSecond, this);
    timer.start();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    paintBorders(g);
    paintInfo(g);
  }

  private void paintBorders(Graphics g) {
    g.setColor(Color.BLACK);
    g.drawRect(
        PADDING - 1, PADDING - 1, (FIELD_HEIGHT - PADDING * 2) + 1,
        (FIELD_HEIGHT - PADDING * 2) + 1);
    paintMap(DATA[currentFrame.getAndIncrement()], g);
  }

  private void paintInfo(Graphics g) {
    g.setColor(Color.BLACK);
    g.setFont(TITLE_FONT);
    g.drawString("Current simulation params", FIELD_HEIGHT, PADDING);
    g.setFont(STANDARD_FONT);
    g.drawString(String.format("FPS: %d", currFramesPerSecond), FIELD_HEIGHT,
        PADDING + LINE_HEIGHT);
  }

  private void paintMap(double[][] heatMap, Graphics g) {

    for (int y = 0; y < HEIGHT; y++) {
      for (int x = 0; x < WIDTH; x++) {

        var color = HeatMapColor.getHeatMapColor(heatMap[x][y], minValue, maxValue);
        g.setColor(color);

        g.fillRect(CELL_SIZE * x + PADDING, CELL_SIZE * y + PADDING, CELL_SIZE,
            CELL_SIZE);
      }
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    if (currentFrame.intValue() >= DATA.length - 1) {
      ((Timer) e.getSource()).stop();
    }
    repaint();
  }
}
