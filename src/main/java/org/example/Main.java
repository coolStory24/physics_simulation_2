package org.example;

import org.example.twodimensions.simulation.Simulation;
import org.example.twodimensions.visualisation.VisualisationFrame;

public class Main {

  public static void main(String[] args) {
    var snapshots = Simulation.simulate();
    new VisualisationFrame(snapshots);
  }
}
