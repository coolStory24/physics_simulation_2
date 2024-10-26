package org.example.onedimension;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class Visualisation extends JFrame {

  public Visualisation(String title, double[] data) {
    super(title);

    // Create dataset
    XYSeriesCollection dataset = createDataset(data);

    // Create chart
    JFreeChart chart = ChartFactory.createXYLineChart(
        "",       // Chart title
        "step",              // X-Axis Label
        "delta x",              // Y-Axis Label
        dataset,          // Dataset
        PlotOrientation.VERTICAL,
        true, true, false);

    // Customize the chart (optional)
    chart.setBackgroundPaint(Color.white);

    // Add the chart to a panel
    ChartPanel panel = new ChartPanel(chart);
    panel.setPreferredSize(new Dimension(800, 600));
    setContentPane(panel);
  }

  private XYSeriesCollection createDataset(double[] data) {
    XYSeries series = new XYSeries("");

    // Add data points to the series
    for (int x = 0; x < data.length; x++) {
      series.add(x, data[x]);
    }

    XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(series);
    return dataset;
  }

  public void visualise() {
    SwingUtilities.invokeLater(() -> {
      this.setSize(1500, 800);
      this.setLocationRelativeTo(null);
      this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      this.setVisible(true);
    });
  }
}
