package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dto.Account;
import ca.jrvs.apps.trading.dto.MarketOrder;
import ca.jrvs.apps.trading.dto.SecurityOrder;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    /**
     * Execute a market order
     * - validate the order (e.g. size and ticker)
     * - create a securityOrder
     * - handle buy or sell orders
     * 	- buy order : check account balance
     * 	- sell order : check position for the ticker/symbol
     * 	- do not forget to update the securityOrder.status
     * - save and return securityOrder
     *
     * NOTE: you are encouraged to make some helper methods (protected or private)
     *
     * @param orderData market order
     * @return SecurityOrder from security_order table
     * @throws DataAccessException if unable to get data from DAO
     * @throws IllegalArgumentException for invalid inputs
     */
    public SecurityOrder executeMarketOrder(MarketOrder orderData) {
        //TODO
    }

    /**
     * Helper method to execute a buy order
     *
     * @param marketOrder user order
     * @param securityOrder to be saved in database
     * @param account account
     */
    protected void handleBuyMarketOrder(MarketOrder marketOrder, SecurityOrder securityOrder, Account account) {
        //TODO
    }

    /**
     * Helper method to execute a sell order
     *
     * @param marketOrder user order
     * @param securityOrder to be saved in database
     * @param account account
     */
    protected void handleSellMarketOrder(MarketOrder marketOrder, SecurityOrder securityOrder, Account account) {
        //TODO
    }

}
