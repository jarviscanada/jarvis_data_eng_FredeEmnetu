package ca.jrvs.apps.stockquote.util;

import ca.jrvs.apps.stockquote.dto.Quote;
import okhttp3.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QuoteHttpHelperUnitTest {

  @Mock
  private OkHttpClient mockClient;

  @Mock
  private Call mockCall;

  private QuoteHttpHelper quoteHttpHelper;

  @Before
  public void setUp() {
    quoteHttpHelper = new QuoteHttpHelper("fake-api-key", mockClient);
  }

  private Response buildResponse(String jsonBody) {
    return new Response.Builder()
        .request(new Request.Builder().url("http://fake.url").build())
        .protocol(Protocol.HTTP_1_1)
        .code(200)
        .message("OK")
        .body(ResponseBody.create(jsonBody, MediaType.get("application/json")))
        .build();
  }

  // ---------- fetchQuoteInfo() ----------

  @Test
  public void fetchQuoteInfo_validSymbol_returnsQuote() throws IOException {
    String json = "{\"Global Quote\": {"
        + "\"01. symbol\": \"AAPL\","
        + "\"05. price\": \"190.00\""
        + "}}";

    when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
    when(mockCall.execute()).thenReturn(buildResponse(json));

    Quote result = quoteHttpHelper.fetchQuoteInfo("AAPL");

    assertNotNull(result);
    assertEquals("AAPL", result.getSymbol());
    assertNotNull(result.getTimestamp());
  }

  @Test(expected = IllegalArgumentException.class)
  public void fetchQuoteInfo_emptySymbol_throwsIllegalArgumentException() {
    quoteHttpHelper.fetchQuoteInfo("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void fetchQuoteInfo_ioExceptionDuringCall_throwsIllegalArgumentException() throws IOException {
    when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
    when(mockCall.execute()).thenThrow(new IOException("Network error"));

    quoteHttpHelper.fetchQuoteInfo("AAPL");
  }
}