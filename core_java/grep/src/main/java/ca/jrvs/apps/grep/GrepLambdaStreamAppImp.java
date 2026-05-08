package ca.jrvs.apps.grep;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
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
    List<String> matchedLines = listFiles(getRootPath())
        .stream()
        .flatMap(f -> readLines(f).stream())
        .filter(line -> containsPattern(line))
        .collect(Collectors.toList());

    writeToFile(matchedLines);

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