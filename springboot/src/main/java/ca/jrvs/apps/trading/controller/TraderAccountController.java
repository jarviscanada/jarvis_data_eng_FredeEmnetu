package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.service.TraderAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trader")
public class TraderAccountController {

    private final TraderAccountService traderAccountService;

    @Autowired
    public TraderAccountController(TraderAccountService traderAccountService) {
        this.traderAccountService = traderAccountService;
    }



}
