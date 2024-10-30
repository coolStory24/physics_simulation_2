package org.example.twodimensions.visualisation;

import java.awt.HeadlessException;
import javax.swing.JFrame;

public class VisualisationFrame extends JFrame {

  private final int FIELD_WIDTH = 1920;
  private final int FIELD_HEIGHT = 1080;
  private final int PADDING = 40;

  public VisualisationFrame(double[][][] data) throws HeadlessException {
    setBounds(0, 0, FIELD_WIDTH, FIELD_HEIGHT);
    add(new HeatMapVisualisation(data, FIELD_WIDTH, FIELD_HEIGHT, PADDING));
    setVisible(true);
  }
}
