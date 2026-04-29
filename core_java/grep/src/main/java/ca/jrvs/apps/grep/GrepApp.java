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
import java.util.stream.Stream;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrepApp implements JavaGrep{
  private String rootPath;
  private String regex;
  private String outFile;
  private static final Logger logger = LoggerFactory.getLogger(GrepApp.class);

  @Override
  public void process() throws IOException {
    List<String> matchedLines = new ArrayList<>();
    List<File> files = listFiles(this.rootPath);

    for(File f : files){ // go through each file
      List<String> lines = readLines(f);
      for(String line : lines){
        if (containsPattern(line)){
          matchedLines.add(line);
        }
      }
    }
    writeToFile(matchedLines);

  }

  @Override
  public List<File> listFiles(String rootDir) {
    if(rootDir == null || Objects.equals(rootDir,"") || isInvalidPath(rootDir)){
      throw new IllegalArgumentException("Directory cannot be null or empty or invalid path");
    }
    List<File> listOfFiles = new ArrayList<>();

    try(Stream<Path> stream = Files.walk(Paths.get(rootDir))){
      stream
          .filter(path -> !Files.isDirectory(path))
          .forEach(path -> listOfFiles.add(path.toFile()));
    }catch (IOException e){
      throw new RuntimeException("Failed to traverse Directory", e);
    }

    return listOfFiles;
  }

  @Override
  public List<String> readLines(File inputFile) {
    List<String> lines = new ArrayList<>();

    try(Stream<String> stream = Files.lines(inputFile.toPath())){
      stream
          .forEach(lines::add);
    } catch (IOException e) {
      System.out.println(e);
      throw new RuntimeException("Unable to read lines: ",e);

    }
    return lines;
  }

  @Override
  public boolean containsPattern(String line) {
    Pattern p = Pattern.compile(this.getRegex());
    Matcher m = p.matcher(line);
    return m.find();
  }

  @Override
  public void writeToFile(List<String> lines) throws IOException {
    String outFile = this.outFile;
    try{
      Files.write(Paths.get(outFile), lines);
    }catch(IOException e){
      throw new RuntimeException("Write to files failed: ", e);
    }
  }
  static boolean isInvalidPath(String rootDir){
    try {
      Path path = Paths.get(rootDir);
    }catch (Exception e){
      return true;
    }
    return false;
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

  public static void main(String... args){
    BasicConfigurator.configure();
    if (args.length != 3){
      throw new IllegalArgumentException("Must contain 3 arguments (regex, rootPath, outFile");
    }
    GrepApp javaGrep = new GrepApp();
    javaGrep.setRegex(args[0]);
    javaGrep.setRootPath(args[1]);
    javaGrep.setOutFile(args[2]);

    try{
      javaGrep.process();
    }catch(Exception exception){
      logger.error("Error: Unable to process", exception);
    }

  }
}