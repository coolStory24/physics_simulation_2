package org.example.visualisation;

import java.awt.HeadlessException;
import javax.swing.JFrame;

public class VisualisationFrame extends JFrame {
  public VisualisationFrame(double[][][] data) throws HeadlessException {
    setBounds(100, 100, 600, 600);
    add(new HeatMapVisualisation(data));
    setVisible(true);
  }
}
