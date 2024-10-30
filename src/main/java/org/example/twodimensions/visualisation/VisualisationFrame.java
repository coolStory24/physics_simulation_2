package org.example.twodimensions.visualisation;

import java.awt.HeadlessException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;
import javax.swing.JFrame;

public class VisualisationFrame extends JFrame {

  private final int FIELD_WIDTH = 1920;
  private final int FIELD_HEIGHT = 1080;
  private final int PADDING = 40;

  public VisualisationFrame(double[][][] data) throws HeadlessException {
    setBounds(0, 0, FIELD_WIDTH, FIELD_HEIGHT);
    add(new HeatMapVisualisation(0.1, data, FIELD_WIDTH, FIELD_HEIGHT, PADDING));
    setVisible(true);
  }

  public VisualisationFrame(String filePath) throws HeadlessException, FileNotFoundException {
    try (Scanner scanner =
        new Scanner(new FileInputStream(filePath)).useLocale(
            Locale.ENGLISH)) {
      double secondsBetweenSteps = scanner.nextDouble();
      int size = scanner.nextInt();
      int frames = scanner.nextInt();
      var data = new double[frames][size][size];

      for (int fr = 0; fr < frames; fr++) {
        for (int i = 0; i < size; i++) {
          for (int j = 0; j < size; j++) {
            int frame = scanner.nextInt();
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            data[frame][x][y] = scanner.nextDouble();
          }
        }
      }

      setBounds(0, 0, FIELD_WIDTH, FIELD_HEIGHT);
      add(new HeatMapVisualisation(secondsBetweenSteps, data, FIELD_WIDTH, FIELD_HEIGHT, PADDING));
      setVisible(true);
    }
  }
}
