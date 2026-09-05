package ca.jrvs.apps.trading.controller;


import ca.jrvs.apps.trading.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/finnhub/ticker/")
public class QuoteController {

  @Autowired
  private QuoteService quoteService;

  @GetMapping("{ticker}")
  public String getTicker(@PathVariable String ticker){
    System.out.println("QuoteController.getTicker()" +  ticker);
    return quoteService.getQuote(ticker);
//
  }
}
