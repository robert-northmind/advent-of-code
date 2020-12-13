package Year2020.Day13;

import Utility.*;
import java.util.*;

class Day_13 {
  public static void main(String[] args) {
    // String filePath = "src/Year2020/Day13/Input/test_4.txt";
    String filePath = "src/Year2020/Day13/Input/full_input.txt";
    MyFileReader fileReader = new MyFileReader(filePath);
    ArrayList<String> lines = fileReader.getInputLines();

    BusDepartureSystem departureSystem = new BusDepartureSystem(lines);
    DepartureInfo departureInfo = departureSystem.getDepartureInfo();
    System.out.println("You need to wait " + departureInfo.waitTimeMin + " min for Bus " + departureInfo.busId
        + ". Answer is: " + departureInfo.busId * departureInfo.waitTimeMin);

    long timeStamp = departureSystem.getTimeStampForSyncedDepartures();
    System.out.println("Timestamp for synchronized departure: " + timeStamp);
  }
}

class BusDepartureSystem {
  int wantedDepartureTime;
  ArrayList<Integer> busIds = new ArrayList<Integer>();
  ArrayList<String> allBusIds = new ArrayList<String>();

  BusDepartureSystem(ArrayList<String> inputStrings) {
    this.wantedDepartureTime = Integer.parseInt(inputStrings.get(0));
    String[] busIdsInputStrings = inputStrings.get(1).split(",");
    for (String busIdsInputString : busIdsInputStrings) {
      if (!busIdsInputString.equals("x")) {
        busIds.add(Integer.parseInt(busIdsInputString));
      }
      allBusIds.add(busIdsInputString);
    }
  }

  DepartureInfo getDepartureInfo() {
    DepartureInfo minDepartureInfo = new DepartureInfo(Integer.MAX_VALUE, -1);
    for (int busId : busIds) {
      int currWaitTime = busId - wantedDepartureTime % busId;
      if (currWaitTime < minDepartureInfo.waitTimeMin) {
        minDepartureInfo.waitTimeMin = currWaitTime;
        minDepartureInfo.busId = busId;
      }
    }
    return minDepartureInfo;
  }

  long getTimeStampForSyncedDepartures() {
    ArrayList<Long> busTimeStampOffsets = new ArrayList<Long>();
    ArrayList<Long> currentBusIds = new ArrayList<Long>();

    for (int i = 0; i < allBusIds.size(); i++) {
      if (allBusIds.get(i).equals("x")) {
        continue; // Ignore x
      }
      long busId = Long.parseLong(allBusIds.get(i));
      busTimeStampOffsets.add(busId - Long.valueOf(i));
      currentBusIds.add(busId);
    }

    long timestamp = ChineseReminder.getChineseRemainder(currentBusIds, busTimeStampOffsets);
    return timestamp;
  }
}

class DepartureInfo {
  int waitTimeMin;
  int busId;

  DepartureInfo(int waitTimeMin, int busId) {
    this.waitTimeMin = waitTimeMin;
    this.busId = busId;
  }
}

class ChineseReminder {
  static long getChineseRemainder(ArrayList<Long> moduloNumbers, ArrayList<Long> reminders) {
    long product = moduloNumbers.stream().reduce(Long.valueOf(1), (i, j) -> i * j);
    long sum = 0;
    for (int i = 0; i < moduloNumbers.size(); i++) {
      long partialProduct = product / moduloNumbers.get(i);
      sum += reminders.get(i) * computeInverse(partialProduct, moduloNumbers.get(i)) * partialProduct;
    }
    return sum % product;
  }

  private static long computeInverse(long a, long b) {
    long b0 = b;
    long x0 = 0;
    long x1 = 1;
    if (b == 1) {
      return 1;
    }
    while (a > 1) {
      long q = a / b;
      long amb = a % b;
      a = b;
      b = amb;
      long xqx = x1 - q * x0;
      x1 = x0;
      x0 = xqx;
    }
    if (x1 < 0) {
      x1 += b0;
    }
    return x1;
  }
}
