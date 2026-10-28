package ca.jrvs.apps.practice;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexExcImp implements RegexExc{
  private static final Pattern jpegPattern = Pattern.compile(".*\\.(jpg|jpeg)$");
  private static final Pattern ipPattern = Pattern.compile("^\\d{3}\\.\\d{3}\\.\\d{3}\\.\\d{3}$");
  private static final Pattern emptyLinePattern = Pattern.compile("^\\s*$");


  @Override
  public boolean matchJpeg(String filename) {
    if(filename == null || Objects.equals(filename,"")) throw new IllegalArgumentException("Filename cannot be null or Empty");

    Matcher m = jpegPattern.matcher(filename);

    return m.matches();
  }

  @Override
  public boolean matchIp(String ip) {
    if(ip == null || Objects.equals(ip, "")) throw new IllegalArgumentException("Ip string cannot be null or empty");

    Matcher m = ipPattern.matcher(ip);

    return m.matches();
  }

  @Override
  public boolean isEmptyLine(String line) {
    if(line == null || Objects.equals(line, "")) throw new IllegalArgumentException("String line cannot be null or empty");

    Matcher m = emptyLinePattern.matcher(line);

    return m.matches();
  }
}
