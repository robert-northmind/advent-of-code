package Year2019.Day12;

import Utility.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Constants {
  static Boolean showLogs = false;
}

class Day_12 {
  public static void main(String[] args) {
    ArrayList<String> filePaths = new ArrayList<String>();
    filePaths.add("src/Year2019/Day12/Input/test_1.txt");
    filePaths.add("src/Year2019/Day12/Input/full_input.txt");

    for (String path : filePaths) {
      System.out.println("path" + path);
      MyFileReader fileReader = new MyFileReader(path);
      ArrayList<String> lines = fileReader.getInputLines();
      PlanetMachine planetMachine = new PlanetMachine(lines);

      // planetMachine.simulateNbrSteps(1000);
      planetMachine.printNeededStepsForRepeat();
    }
  }
}

class PlanetMachine {
  ArrayList<Planet> planets = new ArrayList<Planet>();

  public PlanetMachine(ArrayList<String> lines) {
    for (String line : lines) {
      Planet planet = PlanetFactory.getPlanetFromString(line);
      planets.add(planet);
    }
  }

  public void simulateNbrSteps(int nbrSteps) {
    int currStep = 0;
    printCurrentState(currStep);

    for (int i = 1; i <= nbrSteps; i++) {

      updateVelocity();
      updatePositions();

      printCurrentState(i);
    }

    printTotalEnergy();
  }

  public void printNeededStepsForRepeat() {
    updateVelocity();
    updatePositions();
    long nbrIter = 2;

    Long[] isRepIndex = { 0l, 0l, 0l };

    while (isRepIndex[0] == 0 || isRepIndex[1] == 0 || isRepIndex[2] == 0) {

      updateVelocity();
      updatePositions();

      Boolean[] isRep = isRepeated();
      if (isRepIndex[0] == 0 && isRep[0]) {
        System.out.println("X iter: " + nbrIter);
        isRepIndex[0] = nbrIter;
      }
      if (isRepIndex[1] == 0 && isRep[1]) {
        System.out.println("Y iter: " + nbrIter);
        isRepIndex[1] = nbrIter;
      }
      if (isRepIndex[2] == 0 && isRep[2]) {
        System.out.println("Z iter: " + nbrIter);
        isRepIndex[2] = nbrIter;
      }

      nbrIter += 1;
    }

    System.out.println("isRepIndex: " + isRepIndex[0] + ", " + isRepIndex[1] + ", " + isRepIndex[2]);
    System.out.println("Needed Steps for repeat: " + (nbrIter));
  }

  private Boolean[] isRepeated() {
    Boolean xRep = true;
    Boolean yRep = true;
    Boolean zRep = true;
    for (Planet planet : planets) {
      xRep = xRep ? planet.isRepeatedX() : false;
      yRep = yRep ? planet.isRepeatedY() : false;
      zRep = zRep ? planet.isRepeatedZ() : false;
    }
    Boolean[] isRep = { xRep, yRep, zRep };
    return isRep;
  }

  private void printTotalEnergy() {
    int totalEnergy = 0;
    for (Planet planet : planets) {
      totalEnergy += planet.getTotalEnergy();
    }
    System.out.println("Sum of total energy: " + totalEnergy);
  }

  private void printCurrentState(int currStep) {
    System.out.println("After " + currStep + " steps:");
    for (Planet planet : planets) {
      System.out.println(planet);
    }
    System.out.println("");
  }

  private void updateVelocity() {
    for (int i = 0; i < planets.size() - 1; i++) {
      Planet planet1 = planets.get(i);
      for (int j = i + 1; j < planets.size(); j++) {
        Planet planet2 = planets.get(j);

        if (planet1.position.x < planet2.position.x) {
          planet1.velocity.x += 1;
          planet2.velocity.x -= 1;
        } else if (planet1.position.x > planet2.position.x) {
          planet1.velocity.x -= 1;
          planet2.velocity.x += 1;
        }

        if (planet1.position.y < planet2.position.y) {
          planet1.velocity.y += 1;
          planet2.velocity.y -= 1;
        } else if (planet1.position.y > planet2.position.y) {
          planet1.velocity.y -= 1;
          planet2.velocity.y += 1;
        }

        if (planet1.position.z < planet2.position.z) {
          planet1.velocity.z += 1;
          planet2.velocity.z -= 1;
        } else if (planet1.position.z > planet2.position.z) {
          planet1.velocity.z -= 1;
          planet2.velocity.z += 1;
        }
      }
    }
  }

  private void updatePositions() {
    for (Planet planet : planets) {
      planet.applyVelocityToPosition();
    }
  }
}

class PlanetFactory {
  static Planet getPlanetFromString(String input) {
    String pattern = "<x=(-?\\d+), y=(-?\\d+), z=(-?\\d+)>";
    Matcher matcher = Pattern.compile(pattern).matcher(input);
    if (matcher.find()) {
      int x = Integer.parseInt(matcher.group(1));
      int y = Integer.parseInt(matcher.group(2));
      int z = Integer.parseInt(matcher.group(3));
      return new Planet(x, y, z);
    }
    System.out.println("Error !! No planet coords");
    return null;
  }
}

class Planet {
  Coord initialPosition;
  Coord initialVelocity;
  Coord position;
  Coord velocity;

  public Planet(int x, int y, int z) {
    this.position = new Coord(x, y, z);
    this.initialPosition = new Coord(x, y, z);
    this.velocity = new Coord(0, 0, 0);
    this.initialVelocity = new Coord(0, 0, 0);
  }

  public Boolean isRepeatedX() {
    return this.position.x == this.initialPosition.x && this.velocity.x == this.initialVelocity.x;
  }

  public Boolean isRepeatedY() {
    return this.position.y == this.initialPosition.y && this.velocity.y == this.initialVelocity.y;
  }

  public Boolean isRepeatedZ() {
    return this.position.z == this.initialPosition.z && this.velocity.z == this.initialVelocity.z;
  }

  public int getPotentialEnergy() {
    return Math.abs(position.x) + Math.abs(position.y) + Math.abs(position.z);
  }

  public int getKineticEnergy() {
    return Math.abs(velocity.x) + Math.abs(velocity.y) + Math.abs(velocity.z);
  }

  public int getTotalEnergy() {
    return getPotentialEnergy() * getKineticEnergy();
  }

  public void applyVelocityToPosition() {
    this.position.x += this.velocity.x;
    this.position.y += this.velocity.y;
    this.position.z += this.velocity.z;
  }

  public String toString() {
    return "pos = " + position.toString() + ", vel = " + velocity.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    return this.position == ((Planet) obj).position && this.velocity == ((Planet) obj).velocity;
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }
}

class Coord {
  public int x;
  public int y;
  public int z;

  public Coord(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public String toString() {
    return "<" + this.x + " ," + this.y + " ," + this.z + ">";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    return this.x == ((Coord) obj).x && this.y == ((Coord) obj).y && this.z == ((Coord) obj).z;
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }
}