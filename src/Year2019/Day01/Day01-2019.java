package Year2019.Day01;

import Utility.*;
import java.util.*;

class Day_1 {
  public static void main(String[] args) {
    System.out.println("test 1 Fuel: " + FuelCalculator.fuelAmountForMass(14));
    System.out.println("test 2 Fuel: " + FuelCalculator.fuelAmountForMass(1969));
    System.out.println("test 3 Fuel: " + FuelCalculator.fuelAmountForMass(100756));

    ArrayList<String> filePaths = new ArrayList<String>();
    // filePaths.add("src/Year2019/Day01/Input/test_1.txt");
    filePaths.add("src/Year2019/Day01/Input/full_input.txt");

    int sumFuel = 0;
    for (String path : filePaths) {
      MyFileReader fileReader = new MyFileReader(path);
      ArrayList<String> lines = fileReader.getInputLines();
      // System.out.println("lines: " + lines);
      for (String massStr : lines) {
        sumFuel += FuelCalculator.fuelAmountForMass(Integer.parseInt(massStr));
      }
    }

    System.out.println("sumFuel: " + sumFuel);
  }
}

class FuelCalculator {
  public static int fuelAmountForMass(int mass) {
    int reqFuel = mass / 3 - 2;
    if (reqFuel > 0) {
      reqFuel += Math.max(0, FuelCalculator.fuelAmountForMass(reqFuel));
    }
    return reqFuel;
  }
}