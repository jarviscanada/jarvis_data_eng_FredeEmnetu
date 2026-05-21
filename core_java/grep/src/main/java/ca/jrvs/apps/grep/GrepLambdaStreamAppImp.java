package ca.jrvs.apps.grep;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrepLambdaStreamAppImp extends GrepAppImp implements JavaGrep 

  private static final Logger logger = LoggerFactory.getLogger(GrepLambdaStreamAppImp.class);

  @Override
  public void process() throws IOException {

    try(Stream<Path> files = listFiles(getRootPath())){
      List<String> matchedLines = files
            .flatMap(f -> readLines(f.toFile()))
            .filter(line -> containsPattern(line))
            .collect(Collectors.toList());

      writeToFile(matchedLines);

    }catch(IOException e){

    }

  }
  
  public Stream<Path> listFiles(String rootDir) {
    if (rootDir == null || Objects.equals(rootDir, "") || isInvalidPath(rootDir)) {
      throw new IllegalArgumentException("Directory cannot be null or empty or invalid path");
    }

    

    try {
      Stream<Path> traverse_stream = Files.walk(Paths.get(rootDir));
     return traverse_stream
                .filter(path -> !Files.isDirectory(path))
                .onClose(traverse_stream::close);

    } catch (IOException e) {
      throw new RuntimeException("Failed to traverse Directory", e);
    }
  }

  
  public Stream<String> readLines(File inputFile) {

    try {
      Stream<String> read_stream = Files.lines(inputFile.toPath());
      return read_stream
          .onClose(read_stream::close);
    } catch (IOException e) {
      throw new RuntimeException("Unable to read lines: ", e);

    }
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