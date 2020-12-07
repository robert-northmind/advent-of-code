package Year2020.Day07;

import Utility.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day_7 {
  public static void main(String[] args) {
    ArrayList<String> filePaths = new ArrayList<String>();
    // filePaths.add("src/Year2020/Day07/Input/test_2.txt");
    filePaths.add("src/Year2020/Day07/Input/full_input.txt");

    MyFileReader fileReader = new MyFileReader(filePaths.get(0));
    ArrayList<String> lines = fileReader.getInputLines();

    HashMap<String, LuggageRule> luggageRulesMap = LuggageChecker.parseLuggageRules(lines);
    int bagsThatCanContainYours = LuggageChecker.findBagsThatCanContainYourBag(luggageRulesMap, "shiny gold");
    System.out.println("Number of bags that can contain your: " + bagsThatCanContainYours);

    int individualBagsInside = LuggageChecker.findNbrIndividualBagsInside(luggageRulesMap, "shiny gold");
    System.out.println("Individual bags inside: " + individualBagsInside);
  }
}

class LuggageChecker {
  static int findBagsThatCanContainYourBag(HashMap<String, LuggageRule> luggageRulesMap, String yourBagColor) {
    int bagsThatCanContainYours = 0;
    for (LuggageRule luggageRule : luggageRulesMap.values()) {
      if (!luggageRule.color.equals(yourBagColor)) {
        if (luggageRuleContainsYourBag(luggageRule.color, yourBagColor, luggageRulesMap)) {
          bagsThatCanContainYours += 1;
        }
      }
    }
    return bagsThatCanContainYours;
  }

  static int findNbrIndividualBagsInside(HashMap<String, LuggageRule> luggageRulesMap, String bagColorChecked) {
    LuggageRule initialLuggageRule = luggageRulesMap.get(bagColorChecked);
    if (initialLuggageRule == null) {
      return 0;
    }
    int nbrBags = numberBagsInsideBag(initialLuggageRule, luggageRulesMap) - 1;
    return nbrBags;
  }

  private static int numberBagsInsideBag(LuggageRule luggageRule, HashMap<String, LuggageRule> luggageRulesMap) {
    if (luggageRule == null) {
      return 0;
    }
    int numberBagsInside = 1;
    for (LuggageBagContent allowedContent : luggageRule.allowedContents) {
      LuggageRule contentLuggageRule = luggageRulesMap.get(allowedContent.color);
      numberBagsInside += allowedContent.amount * numberBagsInsideBag(contentLuggageRule, luggageRulesMap);
    }
    return numberBagsInside;

  }

  private static boolean luggageRuleContainsYourBag(String luggageBagColor, String yourBagColor,
      HashMap<String, LuggageRule> luggageRulesMap) {
    LuggageRule luggageRule = luggageRulesMap.get(luggageBagColor);
    if (luggageRule == null) {
      return false;
    }
    for (LuggageBagContent allowedContent : luggageRule.allowedContents) {
      if (allowedContent.color.equals(yourBagColor)) {
        return true;
      } else {
        boolean contentsContainYourColor = luggageRuleContainsYourBag(allowedContent.color, yourBagColor,
            luggageRulesMap);
        if (contentsContainYourColor) {
          return true;
        }
      }
    }
    return false;
  }

  static HashMap<String, LuggageRule> parseLuggageRules(ArrayList<String> inputStrings) {
    HashMap<String, LuggageRule> luggageRulesMap = new HashMap<String, LuggageRule>();
    for (String inputString : inputStrings) {
      LuggageRule luggageRule = new LuggageRule(inputString);
      luggageRulesMap.put(luggageRule.color, luggageRule);
    }
    return luggageRulesMap;
  }
}

class LuggageBagContent {
  int amount;
  String color;

  LuggageBagContent(int amount, String color) {
    this.amount = amount;
    this.color = color;
  }
}

class LuggageRule {
  String color;
  ArrayList<LuggageBagContent> allowedContents = new ArrayList<LuggageBagContent>();

  LuggageRule(String inputString) {
    String bagColorPattern = "(^.+)\\sbags\\scontain\\s(.+)$";
    Matcher bagColorMatcher = Pattern.compile(bagColorPattern).matcher(inputString);
    if (bagColorMatcher.find()) {
      this.color = bagColorMatcher.group(1);
      String contentString = bagColorMatcher.group(2);

      String bagContentPattern = "(\\d)\\s([a-zA-Z\\s]+)\\sbag|bags";
      Matcher bagContentMatcher = Pattern.compile(bagContentPattern).matcher(contentString);
      while (bagContentMatcher.find()) {
        if (bagContentMatcher.group(1) != null && bagContentMatcher.group(2) != null) {
          int amount = Integer.parseInt(bagContentMatcher.group(1));
          String color = bagContentMatcher.group(2);
          this.allowedContents.add(new LuggageBagContent(amount, color));
        }
      }
    }
  }
}
