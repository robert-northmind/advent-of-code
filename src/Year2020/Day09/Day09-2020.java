package Year2020.Day09;

import Utility.*;
import java.util.*;

class Day_9 {
  public static void main(String[] args) {
    // String filePath = "src/Year2020/Day09/Input/test_1.txt";
    String filePath = "src/Year2020/Day09/Input/full_input.txt";
    MyFileReader fileReader = new MyFileReader(filePath);
    ArrayList<String> lines = fileReader.getInputLines();

    // int preambleLength = 5;
    int preambleLength = 25;
    XmasHacker xmasHacker = new XmasHacker(lines);
    long firstInvalid = xmasHacker.getFirstInvalidEncoding(preambleLength);
    System.out.println("First invalid encoding: " + firstInvalid);

    MatchingEncoding matchingConSetMinMax = xmasHacker.findContiguousSet(firstInvalid);
    long matchingConSetSum = matchingConSetMinMax.match1 + matchingConSetMinMax.match2;
    System.out.println("matchin con set min: " + matchingConSetMinMax.match1 + " max: " + matchingConSetMinMax.match2
        + " sum: " + matchingConSetSum);
  }
}

class XmasHacker {
  ArrayList<Long> encodingInput = new ArrayList<Long>();

  XmasHacker(ArrayList<String> encodedStrings) {
    for (String encodedString : encodedStrings) {
      encodingInput.add(Long.parseLong(encodedString));
    }
  }

  MatchingEncoding findContiguousSet(long forEncodingVal) {
    ArrayList<Long> machingValues = new ArrayList<Long>();

    for (int i = 0; i < encodingInput.size(); i++) {
      long initialEncodingVal = encodingInput.get(i);
      long encodingSum = initialEncodingVal;
      machingValues.clear();
      machingValues.add(initialEncodingVal);
      for (int j = i + 1; j < encodingInput.size(); j++) {
        long encodingVal = encodingInput.get(j);
        encodingSum += encodingVal;
        machingValues.add(encodingVal);
        if (encodingSum == forEncodingVal) {
          // We are done! Return
          return findMaxMinInList(machingValues);
        } else if (encodingSum > forEncodingVal) {
          // We went over sum. Stop checking this contiguous set
          break;
        }
      }
    }

    return null;
  }

  private MatchingEncoding findMaxMinInList(ArrayList<Long> values) {
    Long matchedMin = Long.MAX_VALUE;
    Long matchedMax = Long.MIN_VALUE;
    for (Long value : values) {
      if (value < matchedMin) {
        matchedMin = value;
      } else if (value > matchedMax) {
        matchedMax = value;
      }
    }
    return new MatchingEncoding(matchedMin, matchedMax);
  }

  long getFirstInvalidEncoding(int preambleLength) {
    int firstInvalid = -1;
    HashSet<Long> preambles = new HashSet<Long>();
    int trailingPreamblesIndex = 0;

    for (int i = 0; i < encodingInput.size(); i++) {
      if (i < preambleLength) {
        preambles.add(encodingInput.get(i));
      } else {
        long nextEncodingToCheck = encodingInput.get(i);
        MatchingEncoding matchinSums = isEncodingValid(nextEncodingToCheck, preambles);
        if (matchinSums != null) {
          long lastPreambleToRemove = encodingInput.get(trailingPreamblesIndex);
          preambles.remove(lastPreambleToRemove);
          preambles.add(nextEncodingToCheck);
          trailingPreamblesIndex += 1;
        } else {
          return nextEncodingToCheck;
        }
      }
    }

    return firstInvalid;
  }

  private MatchingEncoding isEncodingValid(long encodingToCheck, HashSet<Long> preambles) {
    for (Long preambleVal : preambles) {
      Long otherValueOfSum = encodingToCheck - preambleVal;
      if (otherValueOfSum != preambleVal && preambles.contains(otherValueOfSum)) {
        return new MatchingEncoding(preambleVal, otherValueOfSum);
      }
    }
    return null;
  }
}

class MatchingEncoding {
  Long match1;
  Long match2;

  MatchingEncoding(Long match1, Long match2) {
    this.match1 = match1;
    this.match2 = match2;
  }
}