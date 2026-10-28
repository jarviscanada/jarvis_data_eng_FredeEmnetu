package ca.jrvs.apps.practice;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class LambdaStreamExcImpTest {

  LambdaStreamExcImp LambdaStreamTest = new LambdaStreamExcImp();

  @Test
  void createStrStream_withStrings_returnsStreamOfAllStrings() {
    Stream<String> result = LambdaStreamTest.createStrStream("Alice", "Bob", "Charlie");

    List<String> resultList = result.collect(Collectors.toList());

    assertEquals(3, resultList.size());
    assertEquals(Arrays.asList("Alice", "Bob", "Charlie"), resultList);
  }
  @Test
  public void testPrintMessages() {

    String[] messages = {"a", "b", "c"};
    Consumer<String> printer = LambdaStreamTest.getLambdaPrinter("msg:", "!");

    // Capture stdout
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(outContent));

    LambdaStreamTest.printMessages(messages, printer);

    // Restore stdout
    System.setOut(originalOut);

    String output = outContent.toString();
    assertTrue(output.contains("msg:a!"));
    assertTrue(output.contains("msg:b!"));
    assertTrue(output.contains("msg:c!"));
  }
}
