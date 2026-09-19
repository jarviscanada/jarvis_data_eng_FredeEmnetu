package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.dto.Account;
import ca.jrvs.apps.trading.dto.Trader;
import ca.jrvs.apps.trading.dto.TraderAccountView;
import ca.jrvs.apps.trading.service.TraderAccountService;
import java.time.LocalDate;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/trader")
public class TraderAccountController {

    private final TraderAccountService traderAccountService;

    @Autowired
    public TraderAccountController(TraderAccountService traderAccountService) {
        this.traderAccountService = traderAccountService;
    }

    // Create trader via DTO
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public TraderAccountView createTrader(@RequestBody Trader trader) {
        try {
            return traderAccountService.createTraderAndAccount(trader);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to create trader", e);
        }
    }

    // Create trader via path parameters
    @PostMapping("/firstname/{firstname}/lastname/{lastname}/dob/{dob}/country/{country}/email/{email}")
    @ResponseStatus(HttpStatus.CREATED)
    public TraderAccountView createTrader(
        @PathVariable String firstname,
        @PathVariable String lastname,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dob,
        @PathVariable String country,
        @PathVariable String email) {
        try {
            Trader trader = new Trader();
            trader.setFirstName(firstname);
            trader.setLastName(lastname);
            trader.setDob(dob);
            trader.setCountry(country);
            trader.setEmail(email);
            return traderAccountService.createTraderAndAccount(trader);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to create trader", e);
        }
    }

    // Delete trader
    @DeleteMapping("/traderId/{traderId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTrader(@PathVariable Integer traderId) {
        try {
            traderAccountService.deleteTraderById(traderId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to delete trader", e);
        }
    }

    // Deposit funds
    @PutMapping("/deposit/traderId/{traderId}/amount/{amount}")
    @ResponseStatus(HttpStatus.OK)
    public Account depositFund(@PathVariable Integer traderId, @PathVariable Double amount) {
        try {
            return traderAccountService.deposit(traderId, amount);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to deposit funds", e);
        }
    }

    // Withdraw funds
    @PutMapping("/withdraw/traderId/{traderId}/amount/{amount}")
    @ResponseStatus(HttpStatus.OK)
    public Account withdrawFund(@PathVariable Integer traderId, @PathVariable Double amount) {
        try {
            return traderAccountService.withdraw(traderId, amount);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to withdraw funds", e);
        }
    }
}
