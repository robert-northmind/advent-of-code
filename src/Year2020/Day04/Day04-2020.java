package Year2020.Day04;

import Utility.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day_4 {
  public static void main(String[] args) {
    ArrayList<String> filePaths = new ArrayList<String>();
    // filePaths.add("src/Year2020/Day04/Input/test_3.txt");
    filePaths.add("src/Year2020/Day04/Input/full_input.txt");

    MyFileReader fileReader = new MyFileReader(filePaths.get(0));
    ArrayList<String> lines = fileReader.getInputLines();
    int nbrValid = PassportCheck.findNbrVaildPassports(lines);
    System.out.println("Number of valid passports: " + nbrValid);
  }
}

class PassportCheck {
  public static int findNbrVaildPassports(ArrayList<String> inputStrings) {
    ArrayList<Passport> passports = parseBatchData(inputStrings);
    int numberOfValidPassports = 0;
    for (Passport passport : passports) {
      if (passport.isValid(true)) {
        numberOfValidPassports += 1;
      }
    }
    return numberOfValidPassports;
  }

  private static ArrayList<Passport> parseBatchData(ArrayList<String> inputStrings) {
    ArrayList<Passport> passports = new ArrayList<Passport>();
    String passportInput = "";

    for (String inputStr : inputStrings) {
      if (inputStr.isEmpty()) {
        if (!passportInput.isEmpty()) {
          passports.add(new Passport(passportInput));
        }
        passportInput = "";
      } else {
        passportInput += " " + inputStr;
      }
    }

    if (!passportInput.isEmpty()) {
      passports.add(new Passport(passportInput));
    }

    return passports;
  }
}

class Passport {
  HashMap<String, String> properties = new HashMap<String, String>();

  public Passport(String inputString) {
    String pattern = "(\\w+):(#?\\w+)";
    Matcher matcher = Pattern.compile(pattern).matcher(inputString);
    while (matcher.find()) {
      properties.put(matcher.group(1), matcher.group(2));
    }
  }

  public boolean isValid(boolean ignoreCountyId) {
    return hasValidBirthYear() && hasValidIssueYear() && hasValidExpirationYear() && hasValidHeight()
        && hasValidHairColor() && hasValidEyeColor() && hasValidPassportId();
  }

  private boolean hasValidBirthYear() {
    String valueStr = properties.get(birthYear);
    boolean isValid = false;
    if (valueStr != null) {
      int valueInt = Integer.parseInt(valueStr);
      isValid = valueInt >= 1920 && valueInt <= 2002;
    }
    return isValid;
  }

  private boolean hasValidIssueYear() {
    String valueStr = properties.get(issueYear);
    if (valueStr != null) {
      int valueInt = Integer.parseInt(valueStr);
      return valueInt >= 2010 && valueInt <= 2020;
    }
    return false;
  }

  private boolean hasValidExpirationYear() {
    String valueStr = properties.get(expirationYear);
    if (valueStr != null) {
      int valueInt = Integer.parseInt(valueStr);
      return valueInt >= 2020 && valueInt <= 2030;
    }
    return false;
  }

  private boolean hasValidHeight() {
    String valueStr = properties.get(height);
    boolean isValid = false;
    if (valueStr != null) {
      String pattern = "^(\\d+)(in|cm)$";
      Matcher matcher = Pattern.compile(pattern).matcher(valueStr);
      if (matcher.find()) {
        int heightInt = Integer.parseInt(matcher.group(1));
        if (matcher.group(2).equals("cm")) {
          isValid = heightInt >= 150 && heightInt <= 193;
        } else if (matcher.group(2).equals("in")) {
          isValid = heightInt >= 59 && heightInt <= 76;
        }
      }
    }
    return isValid;
  }

  private boolean hasValidHairColor() {
    String valueStr = properties.get(hairColor);
    boolean isValid = false;
    if (valueStr != null) {
      String pattern = "^#(\\d|[a-f]){6}$";
      Matcher matcher = Pattern.compile(pattern).matcher(valueStr);
      isValid = matcher.find();
    }
    return isValid;
  }

  private boolean hasValidEyeColor() {
    String valueStr = properties.get(eyeColor);
    boolean isValid = false;
    if (valueStr != null) {
      String pattern = "^amb|blu|brn|gry|grn|hzl|oth$";
      Matcher matcher = Pattern.compile(pattern).matcher(valueStr);
      isValid = matcher.find();
    }
    return isValid;
  }

  private boolean hasValidPassportId() {
    String valueStr = properties.get(passportId);
    if (valueStr != null) {
      String pattern = "^\\d{9}$";
      Matcher matcher = Pattern.compile(pattern).matcher(valueStr);
      return matcher.find();
    }
    return false;
  }

  public static String birthYear = "byr";
  public static String issueYear = "iyr";
  public static String expirationYear = "eyr";
  public static String height = "hgt";
  public static String hairColor = "hcl";
  public static String eyeColor = "ecl";
  public static String passportId = "pid";
  public static String countryId = "cid";
}
