package Year2019.Day06;

import Utility.*;
import java.util.*;

class Day_6 {
  public static void main(String[] args) {
    ArrayList<String> filePaths = new ArrayList<String>();
    filePaths.add("src/Year2019/Day06/Input/test_1.txt");
    filePaths.add("src/Year2019/Day06/Input/full_input.txt");

    for (String path : filePaths) {
      System.out.println("path" + path);
      MyFileReader fileReader = new MyFileReader(path);
      ArrayList<String> lines = fileReader.getInputLines();
      OrbitMachine orbitMachine = new OrbitMachine(lines);
      int totalOrbit = orbitMachine.getTotalOrbits();
      System.out.println("totalOrbit: " + totalOrbit);
    }
  }
}

class OrbitMachine {
  HashMap<String, Planet> planetsMap = new HashMap<String, Planet>();

  public OrbitMachine(ArrayList<String> planetOrbitsArray) {
    for (String orbitStr : planetOrbitsArray) {
      String[] planetArray = orbitStr.split("\\)");
      String planet1 = planetArray[0];
      String planet2 = planetArray[1];

      Planet basePlanet = planetsMap.get(planet1);
      if (basePlanet == null) {
        basePlanet = new Planet(planet1);
        planetsMap.put(basePlanet.name, basePlanet);
      }
      Planet orbitingPlanet = planetsMap.get(planet2);
      if (orbitingPlanet == null) {
        orbitingPlanet = new Planet(planet2);
        planetsMap.put(orbitingPlanet.name, orbitingPlanet);
      }

      basePlanet.orbitingPlanets.add(orbitingPlanet);
    }

  }

  public int getTotalOrbits() {
    Planet initialPlanet = planetsMap.get("COM");
    if (initialPlanet != null) {
      return totalOrbitCount(0, initialPlanet);
    } else {
      return 0;
    }
  }

  int totalOrbitCount(int prevCount, Planet planet) {
    int orbitCount = prevCount;
    for (Planet orbitingPlanet : planet.orbitingPlanets) {
      orbitCount += totalOrbitCount(prevCount + 1, orbitingPlanet);
    }
    return orbitCount;
  }
}

class Planet {
  public String name;
  public HashSet<Planet> orbitingPlanets = new HashSet<Planet>();

  public Planet(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}