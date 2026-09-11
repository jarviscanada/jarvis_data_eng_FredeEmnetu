package ca.jrvs.apps.trading.controller;


import ca.jrvs.apps.trading.ExceptionUtil;
import ca.jrvs.apps.trading.ResourceNotFoundException;
import ca.jrvs.apps.trading.dto.Quote;
import ca.jrvs.apps.trading.service.QuoteService;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quote")
public class QuoteController {

  private final QuoteService quoteService;

  @Autowired
  public QuoteController(QuoteService quoteService) {
    this.quoteService = quoteService;
  }

  @GetMapping("/finnhub/ticker/{ticker}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Quote getQuote(@PathVariable String ticker) throws IOException {
    if (ticker == null || ticker.isEmpty()) {
      throw new IllegalArgumentException(ExceptionUtil.buildMessage("Ticker is null"));
    }
    return quoteService.findQuoteByTicker(ticker);
  }
  @PutMapping("/FinnhubMarketData/{ticker}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Quote AddQuoteByTicker(@PathVariable String ticker){
    if (ticker == null || ticker.isEmpty()) {
      throw new IllegalArgumentException(ExceptionUtil.buildMessage("Ticker cannot be null or empty"));
    }
    try{
      Quote quote = quoteService.findQuoteByTicker(ticker);
      return quoteService.save(quote);
    }catch (Exception e){
      throw new ResourceNotFoundException(ExceptionUtil.buildMessage("Error adding quote"));
    }
  }

  @PutMapping("/")
  public void updateQuote(@RequestBody Quote quote){
    if (quote == null) {
      throw new IllegalArgumentException(ExceptionUtil.buildMessage("Quote is null"));
    }
    try{
      quoteService.updateMarketData(quote);
    }catch (Exception e){
      throw new ResourceNotFoundException(ExceptionUtil.buildMessage("Error updating quote"));
    }
  }

  @PostMapping("/tickerId/{tickerId}")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public Quote createQuote(String tickerId) {
    if  (tickerId == null || tickerId.isEmpty()) {
        throw new IllegalArgumentException(ExceptionUtil.buildMessage("Ticker cannot be null or empty"));
    }

    Quote quote = quoteService.findQuoteByTicker(tickerId);
    if (quote == null) {
      throw new ResourceNotFoundException("Error retrieving quote");
    }
    quoteService.save(quote);
    return quote;
  }

  @GetMapping("/dailyList")
  @ResponseStatus(HttpStatus.OK)
  public List<Quote> getDailyList() {
    return quoteService.findAllQuotes();
  }


}
