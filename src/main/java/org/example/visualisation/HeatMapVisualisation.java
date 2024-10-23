package org.example.visualisation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;
public class HeatMapVisualisation  extends JPanel implements ActionListener {
  private final int FRAMES_PER_SECOND = 20;

  private final double[][][] data;
  private double MIN_VALUE = Double.POSITIVE_INFINITY;
  private double MAX_VALUE = Double.NEGATIVE_INFINITY;

  private final AtomicInteger currentFrame = new AtomicInteger(0);

  public HeatMapVisualisation(double[][][] data) {
    this.data = data;

    for (double[][] datum : data) {
      for (int j = 0; j < data[0].length; j++) {
        for (int k = 0; k < data[0][0].length; k++) {
          MIN_VALUE = Math.min(MIN_VALUE, datum[j][k]);
          MAX_VALUE = Math.max(MAX_VALUE, datum[j][k]);
        }
      }
    }

    setFocusable(true);

    Timer timer = new Timer(1000 / FRAMES_PER_SECOND, this);
    timer.start();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    paintBorders(g);

  }

  public void paintBorders(Graphics g) {
    g.setColor(Color.BLACK);
    g.drawRect(
        0, 0, 600, 600);
    paintMap(data[currentFrame.getAndIncrement()], g);
  }

  private void paintMap(double[][] heatMap, Graphics g) {

    for (int y = 0; y < heatMap.length; y++) {
      for (int x = 0; x < heatMap.length; x++) {

        var color = HeatMapColor.getHeatMapColor(heatMap[x][y], MIN_VALUE, MAX_VALUE);
        g.setColor(color);

        g.fillRect(6*x, 6*y, 6, 6);
      }
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    if (currentFrame.intValue() >= data.length -1) {
      ((Timer)e.getSource()).stop();
    }
    repaint();
  }
}
