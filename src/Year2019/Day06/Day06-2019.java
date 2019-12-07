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

      // int totalOrbit = orbitMachine.getTotalOrbits();
      // System.out.println("totalOrbit: " + totalOrbit);

      // int santaCount = orbitMachine.findSantaCount();
      // System.out.println("santaCount: " + santaCount);

      int santaCount = orbitMachine.printAllParents();
      System.out.println("santaCount: " + santaCount);
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
        basePlanet = new Planet(planet1, null);
        planetsMap.put(basePlanet.name, basePlanet);
      }
      Planet orbitingPlanet = planetsMap.get(planet2);
      if (orbitingPlanet == null) {
        orbitingPlanet = new Planet(planet2, basePlanet);
        planetsMap.put(orbitingPlanet.name, orbitingPlanet);
      }
      orbitingPlanet.orbitsAroundPlanet = basePlanet;

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

  public int printAllParents() {
    System.out.println("\n\nYou planets\n\n");
    Planet youPlanet = planetsMap.get("YOU");
    HashMap<String, Integer> you = getPlanetParents(youPlanet);

    System.out.println("\n\nSanta planets\n\n");

    Planet sanPlanet = planetsMap.get("SAN");
    HashMap<String, Integer> santa = getPlanetParents(sanPlanet);

    int minDist = Integer.MAX_VALUE;
    for (String key : you.keySet()) {
      Integer distMe = you.get(key);
      Integer distOther = santa.get(key);
      if (distOther != null) {
        if (distMe + distOther < minDist) {
          minDist = distMe + distOther;
        }
      }
    }
    return minDist;
  }

  HashMap<String, Integer> getPlanetParents(Planet planet) {
    HashMap<String, Integer> parents = new HashMap<String, Integer>();
    int index = 0;
    while (planet.orbitsAroundPlanet != null) {
      planet = planet.orbitsAroundPlanet;
      parents.put(planet.name, index);
      index += 1;
    }
    return parents;
  }

  public int findSantaCount() {
    HashSet<Planet> checkedPlanets = new HashSet<Planet>();
    Planet youPlanet = planetsMap.get("YOU");
    Planet initialPlanet = youPlanet.orbitsAroundPlanet;
    checkedPlanets.add(youPlanet);

    SantaCheckResult checkResult = new SantaCheckResult(0, false);
    checkResult = findSantaTotalCount(checkResult, initialPlanet, checkedPlanets);
    System.out.println("Found santa: " + checkResult.foundSanta + " count: " + checkResult.count);
    return checkResult.count;
  }

  SantaCheckResult findSantaTotalCount(SantaCheckResult checkResult, Planet planet, HashSet<Planet> checkedPlanets) {
    if (checkedPlanets.contains(planet)) {
      return new SantaCheckResult(checkResult.count, false);
    }
    checkedPlanets.add(planet);
    System.out.println("Checking planet: " + planet.name);
    if (isSantaOrbitingPlanet(planet)) {
      return new SantaCheckResult(checkResult.count, true);
    }

    // Santa not here. search kids.
    for (Planet orbPlanet : planet.orbitingPlanets) {
      SantaCheckResult orbCheckResult = new SantaCheckResult(checkResult.count + 1, false);
      orbCheckResult = findSantaTotalCount(orbCheckResult, orbPlanet, checkedPlanets);
      if (orbCheckResult.foundSanta) {
        return new SantaCheckResult(orbCheckResult.count, true);
      }
    }

    // Still not found santa. check parents
    SantaCheckResult parentCheckResult = new SantaCheckResult(checkResult.count + 1, false);
    SantaCheckResult parentCheckResult1 = findSantaTotalCount(parentCheckResult, planet.orbitsAroundPlanet,
        checkedPlanets);
    return new SantaCheckResult(parentCheckResult1.count, parentCheckResult1.foundSanta);
  }

  Boolean isSantaOrbitingPlanet(Planet planet) {
    for (Planet orbitingPlanet : planet.orbitingPlanets) {
      if (orbitingPlanet.name.equals("SAN")) {
        return true;
      }
    }
    return false;
  }
}

class SantaCheckResult {
  public int count;
  public Boolean foundSanta;

  public SantaCheckResult(int count, Boolean foundSanta) {
    this.count = count;
    this.foundSanta = foundSanta;
  }
}

class Planet {
  public String name;
  public Planet orbitsAroundPlanet;
  public HashSet<Planet> orbitingPlanets = new HashSet<Planet>();

  public Planet(String name, Planet orbitsAroundPlanet) {
    this.name = name;
    this.orbitsAroundPlanet = orbitsAroundPlanet;
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