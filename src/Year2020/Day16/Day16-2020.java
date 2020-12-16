package Year2020.Day16;

import Utility.*;
import java.util.*;
import java.util.regex.*;

class Day_16 {
  public static void main(String[] args) {
    // String filePath = "src/Year2020/Day16/Input/test_2.txt";
    String filePath = "src/Year2020/Day16/Input/full_input.txt";
    MyFileReader fileReader = new MyFileReader(filePath);
    ArrayList<String> lines = fileReader.getInputLines();

    TicketValidator ticketValidator = new TicketValidator();
    int ticketErrorRate = ticketValidator.getTicketErrorRate(lines);
    System.out.println("Ticket scanning error rate: " + ticketErrorRate);

    long departureSectionProduct = ticketValidator.getDepartureSectionProduct(lines);
    System.out.println("Departure Section Product for your ticket: " + departureSectionProduct);
  }
}

class TicketValidator {
  long getDepartureSectionProduct(ArrayList<String> inputLines) {
    TrainTicketSections ticketSections = new TrainTicketSections(inputLines);
    TrainTicket yourTicket = TrainTicket.getYourTicket(inputLines);
    ArrayList<TrainTicket> nearbyTickets = TrainTicket.getNearbyTickets(inputLines);

    ArrayList<TrainTicket> validNearbyTickets = new ArrayList<TrainTicket>();
    for (TrainTicket nearbyTicket : nearbyTickets) {
      if (ticketSections.isTicketValid(nearbyTicket)) {
        validNearbyTickets.add(nearbyTicket);
      }
    }

    ticketSections.computeSectionIndices(validNearbyTickets);
    ArrayList<Integer> departureIndices = ticketSections.getIndicesForDepartureSections();

    long departureSectionProducts = 1;
    for (Integer departureIndex : departureIndices) {
      departureSectionProducts *= yourTicket.encodedNumbers.get(departureIndex);
    }

    return departureSectionProducts;
  }

  int getTicketErrorRate(ArrayList<String> inputLines) {
    TrainTicketSections ticketSections = new TrainTicketSections(inputLines);
    ArrayList<TrainTicket> nearbyTickets = TrainTicket.getNearbyTickets(inputLines);

    int invalidNearbyTicketNbrs = 0;
    for (TrainTicket ticket : nearbyTickets) {
      invalidNearbyTicketNbrs += ticketSections.getInvalidTicketNumbers(ticket).stream().reduce(0, (i, j) -> i + j);
    }

    return invalidNearbyTicketNbrs;
  }
}

class TrainTicket {
  ArrayList<Integer> encodedNumbers = new ArrayList<Integer>();

  TrainTicket(String input) {
    for (String sectionNumber : input.split(",")) {
      encodedNumbers.add(Integer.parseInt(sectionNumber));
    }
  }

  static TrainTicket getYourTicket(ArrayList<String> lines) {
    for (String input : lines) {
      String pattern = "^(\\d+,){1,}\\d+$";
      Matcher matcher = Pattern.compile(pattern).matcher(input);
      if (matcher.find()) {
        String ticketInputString = matcher.group(0);
        return new TrainTicket(ticketInputString);
      }
    }
    return null;
  }

  static ArrayList<TrainTicket> getNearbyTickets(ArrayList<String> lines) {
    String pattern = "^(\\d+,){1,}\\d+$";
    ArrayList<TrainTicket> nearbyTickets = new ArrayList<TrainTicket>();
    boolean isFirst = true;
    for (String input : lines) {
      Matcher matcher = Pattern.compile(pattern).matcher(input);
      if (matcher.find()) {
        if (isFirst) {
          isFirst = false;
          continue;
        }
        String ticketInputString = matcher.group(0);
        nearbyTickets.add(new TrainTicket(ticketInputString));
      }
    }
    return nearbyTickets;
  }

  public String toString() {
    return "{ numbers: " + encodedNumbers + " }";
  }
}

class TrainTicketSections {
  private ArrayList<TrainTicketSection> sections = new ArrayList<TrainTicketSection>();
  private HashSet<Integer> validSectionNumbersSet = new HashSet<Integer>();
  HashMap<Integer, String> sectionIndexMap = new HashMap<Integer, String>();

  TrainTicketSections(ArrayList<String> lines) {
    String pattern = "^(.+): (\\d+)-(\\d+) or (\\d+)-(\\d+)$";
    for (String input : lines) {
      Matcher matcher = Pattern.compile(pattern).matcher(input);
      if (matcher.find()) {
        TrainTicketSection section = new TrainTicketSection(matcher.group(0));
        sections.add(section);
        for (Range<Integer> validRange : section.validNumberRanges) {
          for (int i = validRange.low; i <= validRange.high; i++) {
            validSectionNumbersSet.add(i);
          }
        }
      }
    }
  }

  ArrayList<Integer> getIndicesForDepartureSections() {
    ArrayList<Integer> indices = new ArrayList<Integer>();
    for (Integer sectionIndex : sectionIndexMap.keySet()) {
      if (sectionIndexMap.get(sectionIndex).contains("departure")) {
        indices.add(sectionIndex);
      }
    }
    return indices;
  }

  void computeSectionIndices(ArrayList<TrainTicket> tickets) {
    HashMap<String, HashSet<Integer>> possibleIndciesMap = new HashMap<String, HashSet<Integer>>();

    for (int i = 0; i < tickets.get(0).encodedNumbers.size(); i++) {
      for (TrainTicketSection section : sections) {
        boolean allNumbersValidForThisSection = true;
        for (TrainTicket ticket : tickets) {
          if (!section.isSectionNumberValid(ticket.encodedNumbers.get(i))) {
            allNumbersValidForThisSection = false;
            break;
          }
        }

        if (allNumbersValidForThisSection) {
          HashSet<Integer> possibleIndices = possibleIndciesMap.containsKey(section.name)
              ? possibleIndciesMap.get(section.name)
              : new HashSet<Integer>();
          possibleIndices.add(i);
          possibleIndciesMap.put(section.name, possibleIndices);
        }
      }
    }

    while (possibleIndciesMap.size() > 0) {
      for (String sectionName : possibleIndciesMap.keySet()) {
        if (possibleIndciesMap.get(sectionName).size() == 1) {
          int matchedIndex = possibleIndciesMap.get(sectionName).iterator().next();
          sectionIndexMap.put(matchedIndex, sectionName);
          possibleIndciesMap.remove(sectionName);

          for (HashSet<Integer> possibleIndices : possibleIndciesMap.values()) {
            possibleIndices.remove(matchedIndex);
          }

          break;
        }
      }
    }
  }

  boolean isTicketValid(TrainTicket trainTicket) {
    for (Integer encodedNumber : trainTicket.encodedNumbers) {
      if (!validSectionNumbersSet.contains(encodedNumber)) {
        return false;
      }
    }
    return true;
  }

  ArrayList<Integer> getInvalidTicketNumbers(TrainTicket trainTicket) {
    ArrayList<Integer> invalidNumbers = new ArrayList<Integer>();
    for (Integer encodedNumber : trainTicket.encodedNumbers) {
      if (!validSectionNumbersSet.contains(encodedNumber)) {
        invalidNumbers.add(encodedNumber);
      }
    }
    return invalidNumbers;
  }
}

class TrainTicketSection {
  String name;
  int sectionIndex = -1;
  ArrayList<Range<Integer>> validNumberRanges = new ArrayList<Range<Integer>>();

  TrainTicketSection(String line) {
    String pattern = "^(.+): (\\d+)-(\\d+) or (\\d+)-(\\d+)$";
    Matcher matcher = Pattern.compile(pattern).matcher(line);
    if (matcher.find()) {
      this.name = matcher.group(1);
      int range1Low = Integer.parseInt(matcher.group(2));
      int range1High = Integer.parseInt(matcher.group(3));
      int range2Low = Integer.parseInt(matcher.group(4));
      int range2High = Integer.parseInt(matcher.group(5));
      validNumberRanges.add(new Range<Integer>(range1Low, range1High));
      validNumberRanges.add(new Range<Integer>(range2Low, range2High));
    }
  }

  boolean isSectionNumberValid(int sectionNumber) {
    for (Range<Integer> validRange : validNumberRanges) {
      if (validRange.contains(sectionNumber)) {
        return true;
      }
    }
    return false;
  }

  static Matcher matchStringForSectionContent(String input) {
    String pattern = "^(.+): (.+)$";
    Matcher matcher = Pattern.compile(pattern).matcher(input);
    return matcher.find() ? matcher : null;
  }

  public String toString() {
    return "{ name: " + name + ", ranges: " + validNumberRanges + " }";
  }
}
