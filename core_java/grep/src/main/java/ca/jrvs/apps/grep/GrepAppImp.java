package ca.jrvs.apps.grep;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrepAppImp implements JavaGrep {

  private String rootPath;
  private String regex;
  private String outFile;
  private Pattern pattern;
  private static final Logger logger = LoggerFactory.getLogger(GrepAppImp.class);

  @Override
  public void process() throws IOException {
    List<String> matchedLines = new ArrayList<>();
    List<File> files = listFiles(this.rootPath);

    for (File f : files) { // go through each file
      List<String> lines = readLines(f);
      for (String line : lines) {
        if (containsPattern(line)) {
          matchedLines.add(line);
        }
      }
    }
    writeToFile(matchedLines);

  }

  @Override
  public List<File> listFiles(String rootDir) {
    if (rootDir == null || Objects.equals(rootDir, "") || isInvalidPath(rootDir)) {
      throw new IllegalArgumentException("Directory cannot be null or empty or invalid path");
    }
    List<File> listOfFiles = new ArrayList<>();

    try (Stream<Path> stream = Files.walk(Paths.get(rootDir))) {
      stream
          .filter(path -> !Files.isDirectory(path))
          .forEach(path -> listOfFiles.add(path.toFile()));
    } catch (IOException e) {
      throw new RuntimeException("Failed to traverse Directory", e);
    }

    return listOfFiles;
  }

  @Override
  public List<String> readLines(File inputFile) {
    List<String> lines = new ArrayList<>();

    try (Stream<String> stream = Files.lines(inputFile.toPath())) {
      stream
          .forEach(lines::add);
    } catch (IOException e) {
      throw new RuntimeException("Unable to read lines: ", e);

    }
    return lines;
  }

  @Override
  public boolean containsPattern(String line) {
    
    Matcher m = pattern.matcher(line);
    return m.find();
  }

  @Override
  public void writeToFile(List<String> lines) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.outFile))) {
      for (String line : lines) {
        writer.write(line);
        writer.newLine();
      }
    } catch (IOException e) {
      throw new RuntimeException("Write to files failed: ", e);
    }
  }

  static boolean isInvalidPath(String rootDir) {
    try {
      Path path = Paths.get(rootDir);
    } catch (Exception e) {
      return true;
    }
    return false;
  }

  @Override
  public String getRootPath() {
    return this.rootPath;
  }

  @Override
  public void setRootPath(String rootPath) {
    this.rootPath = rootPath;
  }

  @Override
  public String getRegex() {
    return this.regex;
  }

  @Override
  public void setRegex(String regex) {
    pattern = Pattern.compile(regex);
    this.regex = regex;
  }

  @Override
  public String getOutFile() {
    return this.outFile;
  }

  @Override
  public void setOutFile(String outFile) {
    this.outFile = outFile;
  }

  public static void main(String... args) {
    BasicConfigurator.configure();
    if (args.length != 3) {
      throw new IllegalArgumentException("Must contain 3 arguments (regex, rootPath, outFile)");
    }
    GrepAppImp javaGrep = new GrepAppImp();
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