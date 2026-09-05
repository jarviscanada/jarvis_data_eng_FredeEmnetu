package ca.jrvs.apps.trading.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuoteService {

//  @Autowired
//  private QuoteRepo quoteRepo;

  public String getQuote(String ticker){
    System.out.println("Fetch would go here");
    return "You requested: " + ticker;
  }

}
