package org.example;

import java.io.FileNotFoundException;
import org.example.twodimensions.simulation.Simulation;
import org.example.twodimensions.visualisation.VisualisationFrame;

public class Main {

  public static void main(String[] args) throws FileNotFoundException {
    var snapshots = Simulation.simulate();
    new VisualisationFrame(snapshots);
//    new VisualisationFrame("src/main/resources/file.txt");
  }
}
