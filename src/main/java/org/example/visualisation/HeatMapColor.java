package org.example.visualisation;

import java.awt.Color;

public class HeatMapColor {
  private static final Color[] COLORS = {
      Color.WHITE,         // 0
      Color.BLUE,          // 1/5
      Color.GREEN,         // 2/5
      Color.YELLOW,        // 3/5
      Color.RED,           // 4/5
      new Color(139, 0, 0)
  };

  public static Color getHeatMapColor(double value, double min, double max) {
    double scaledValue = scaleValue(value, min, max);

    int colorIndex = (int) (scaledValue * (COLORS.length - 1));
    double fraction = scaledValue * (COLORS.length - 1) - colorIndex;

    Color color1 = COLORS[colorIndex];
    Color color2 = COLORS[Math.min(colorIndex + 1, COLORS.length - 1)];

    return interpolateColor(color1, color2, fraction);
  }

  private static double scaleValue(double value, double min, double max) {
    double k = 10.0;
    double normalizedValue = k * (value - min) / (max - min);

    return Math.min(normalizedValue, 1);
  }

  private static Color interpolateColor(Color c1, Color c2, double fraction) {
    int r = (int) (c1.getRed() + fraction * (c2.getRed() - c1.getRed()));
    int g = (int) (c1.getGreen() + fraction * (c2.getGreen() - c1.getGreen()));
    int b = (int) (c1.getBlue() + fraction * (c2.getBlue() - c1.getBlue()));
    return new Color(r, g, b);
  }
}

