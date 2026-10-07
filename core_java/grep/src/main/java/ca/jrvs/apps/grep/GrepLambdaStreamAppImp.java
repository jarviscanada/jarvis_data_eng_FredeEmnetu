package ca.jrvs.apps.grep;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrepLambdaStreamAppImp extends GrepAppImp implements JavaGrep {

  private static final Logger logger = LoggerFactory.getLogger(GrepLambdaStreamAppImp.class);

  @Override
  public void process() throws IOException {

    try (Stream<Path> files = listFilesStream(getRootPath())) {
      List<String> matchedLines = files
            .flatMap(f -> readLinesStream(f.toFile()))
            .filter(line -> containsPattern(line))
            .collect(Collectors.toList());

      writeToFile(matchedLines);

    } catch (IOException e) {
      throw new IOException("process failed", e);
    }

  }
  
  public Stream<Path> listFilesStream(String rootDir) {
    if (rootDir == null || Objects.equals(rootDir, "") || isInvalidPath(rootDir)) {
      throw new IllegalArgumentException("Directory cannot be null or empty or invalid path");
    }

    try {
      Stream<Path> traverseStream = Files.walk(Paths.get(rootDir));
     return traverseStream
                .filter(path -> !Files.isDirectory(path))
                .onClose(traverseStream::close);

    } catch (IOException e) {
      throw new RuntimeException("Failed to traverse Directory", e);
    }
  }

  public Stream<String> readLinesStream(File inputFile) {

    try {
      Stream<String> readStream = Files.lines(inputFile.toPath());
      return readStream
          .onClose(readStream::close);
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