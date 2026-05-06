package ca.jrvs.apps.grep;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrepLambdaStreamAppImp extends GrepAppImp implements JavaGrep {
  private String rootPath;
  private String regex;
  private String outFile;

  private static final Logger logger = LoggerFactory.getLogger(GrepLambdaStreamAppImp.class);

  @Override
  public void process() throws IOException {
    List<String> matchedLines = listFiles(this.rootPath)
        .stream()
        .flatMap(f -> readLines(f).stream())
        .filter(line -> containsPattern(line))
        .collect(Collectors.toList());

    writeToFile(matchedLines);

  }

  @Override
  public String getRootPath() {
    return rootPath;
  }

  @Override
  public void setRootPath(String rootPath) {
    this.rootPath = rootPath;
  }

  @Override
  public String getRegex() {
    return regex;
  }

  @Override
  public void setRegex(String regex) {
    this.regex = regex;
  }

  @Override
  public String getOutFile() {
    return outFile;
  }

  @Override
  public void setOutFile(String outFile) {
    this.outFile = outFile;
  }

  public static void main(String... args) {
    BasicConfigurator.configure();
    if (args.length != 3) {
      throw new IllegalArgumentException("Must contain 3 arguments (regex, rootPath, outFile");
    }
    GrepLambdaStreamAppImp javaGrep = new GrepLambdaStreamAppImp();
    javaGrep.setRegex(args[0]);
    javaGrep.setRootPath(args[1]);
    javaGrep.setOutFile(args[2]);

    try {
      javaGrep.process();
    } catch (Exception exception) {
      logger.error("Error: Unable to process", exception);
    }

  }
}