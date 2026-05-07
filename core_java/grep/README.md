# Java Grep Application

## Introduction
The Java Grep application mimics the behavior of the Linux `grep` command — it recursively searches through all 
files in a given directory, matches lines against a regular expression, and writes the matched lines to an output file. 
The app is built using Core Java with the `java.nio` file API, Java Streams, and the `java.util.regex` package for pattern matching. 
SLF4J and Log4j handle logging. The project was developed in IntelliJ IDEA and containerized using Docker, with the image published to 
Docker Hub based on an Alpine Linux base image for a minimal footprint.

---

## Quick Start 

**Run via Docker:**
```bash
docker pull FredeEmnetu/grep-app
docker run --rm \
  -v /path/to/search:/data/input \
  -v /path/to/output:/data/output \
  FredeEmnetu/grep-app \
  ".*your-regex.*" /data/input /data/output/results.txt
```

**Arguments (in order):**
1. `regex` — the pattern to search for
2. `rootPath` — directory to search recursively
3. `outFile` — path to write matched lines

---

## Implementation

### Pseudocode
```
process():
  matchedLines = []
  files = listFiles(rootPath)         // recursively collect all non-directory files
  for each file in files:
    lines = readLines(file)           // read all lines from the file
    for each line in lines:
      if containsPattern(line):       // check line against compiled regex
        matchedLines.add(line)
  writeToFile(matchedLines)           // write all matched lines to outFile
```

### Performance Issue
Loading all lines from a file into a `List<String>` via `readLines()` holds the entire file content in heap memory at once. 
For large files, this risks `OutOfMemoryError`. The fix is to increase the JVM heap size at runtime using `-Xmx` (e.g., `java -Xmx512m -jar grep.jar`), 
or to refactor `readLines` to return a lazy `Stream<String>` so lines are processed and discarded one at a time without full materialization.

---

## Test

Testing was done manually in IntelliJ IDEA using small sample text files. 
A directory of `.txt` files was prepared with known content — some lines matching the target regex and some not. 
The app was run with a specific regex pattern, and the output file was inspected to verify that only the expected lines were captured. 
Edge cases tested included an empty directory, files with no matches, and a regex matching every line.

---

## Deployment

The application is packaged as a JAR and containerized using Docker. 
A `Dockerfile` copies the JAR into an Alpine-based Java image, sets the entrypoint to run the JAR, 
and passes the three required arguments at runtime via `docker run`. The image is built and pushed to Docker Hub:

```bash
docker build -t FredeEmnetu/grep-app .
docker push FredeEmnetu/grep-app
```

This allows the app to run consistently across environments without requiring a local JDK installation.

---

## Improvement 

1. **Stream-based line processing** — refactor `readLines` to return `Stream<String>` instead of `List<String>` so lines are never all held in memory simultaneously, naturally resolving the heap issue without relying on JVM flags.
2. **Parallel file processing** — use a parallel stream or `ExecutorService` in `process()` to search multiple files concurrently, improving throughput on large directory trees.
3. **Regex validation on startup** — currently, an invalid regex pattern will throw at match time. Validating and failing fast in `main()` before any file traversal begins would give the user a cleaner error message.