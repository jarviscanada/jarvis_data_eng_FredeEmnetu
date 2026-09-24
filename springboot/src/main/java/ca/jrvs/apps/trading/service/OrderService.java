package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.ExceptionUtil;
import ca.jrvs.apps.trading.dto.Account;
import ca.jrvs.apps.trading.dto.MarketOrder;
import ca.jrvs.apps.trading.dto.Position;
import ca.jrvs.apps.trading.dto.SecurityOrder;
import ca.jrvs.apps.trading.repository.AccountJpaRepoDao;
import ca.jrvs.apps.trading.repository.PositionJpaRepoDao;
import ca.jrvs.apps.trading.repository.SecurityOrderJpaRepoDao;
import ca.jrvs.apps.trading.repository.TraderJpaRepoDao;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final AccountJpaRepoDao accountRepo;
    private final SecurityOrderJpaRepoDao securityOrderRepo;
    private final TraderJpaRepoDao traderRepo;
    private final QuoteService quoteService;
    private final PositionJpaRepoDao positionRepo;

    @Autowired
    public OrderService(AccountJpaRepoDao accountRepo, SecurityOrderJpaRepoDao securityOrderRepo,
        TraderJpaRepoDao traderRepo, PositionJpaRepoDao positionRepo, QuoteService quoteService) {
        this.accountRepo = accountRepo;
        this.securityOrderRepo = securityOrderRepo;
        this.traderRepo = traderRepo;
        this.positionRepo = positionRepo;
        this.quoteService = quoteService;
    }


    public static <T> boolean allFilled(T obj){
        return Arrays.stream(obj.getClass().getDeclaredFields())
            .peek(f -> f.setAccessible(true))
            .allMatch(f -> {
                try {
                    Object v = f.get(obj);
                    return v != null && !v.toString().trim().isEmpty();
                } catch (IllegalAccessException e) {
                    return false;
                }
            });
    }
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
        if(!allFilled(orderData)) {
            throw new IllegalArgumentException(ExceptionUtil.buildMessage("Market order contains empty fields"));
        }

        if(orderData.getSize() <= 0){
            throw new IllegalArgumentException(ExceptionUtil.buildMessage("Market order size cannot be less than 0"));
        }
        if(orderData.getTicker().isEmpty() || orderData.getTicker() == null){
            throw new IllegalArgumentException(ExceptionUtil.buildMessage("Market order contains empty ticker"));
        }


        SecurityOrder securityOrder = new SecurityOrder();
        Optional<Account> optionalAccount = accountRepo.findById(orderData.getTraderId());
        if(!optionalAccount.isPresent()){
            throw new EmptyResultDataAccessException(1);
        }
        Account account = optionalAccount.get();

        securityOrder.setAccountId(account.getId());
        securityOrder.setTicker(orderData.getTicker());
        securityOrder.setSize(orderData.getSize());
        securityOrder.setStatus("Created");

        if(orderData.getOption() == MarketOrder.Option.BUY ) {
            handleBuyMarketOrder(orderData, securityOrder, account);
        }else if(orderData.getOption() == MarketOrder.Option.SELL){
            handleSellMarketOrder(orderData, securityOrder, account);
        }
        return  securityOrderRepo.save(securityOrder);
    }

    /**
     * Helper method to execute a buy order
     *
     * @param marketOrder user order
     * @param securityOrder to be saved in database
     * @param account account
     */
    protected void handleBuyMarketOrder(MarketOrder marketOrder, SecurityOrder securityOrder, Account account) {
        double price =  quoteService.findQuoteByTicker(marketOrder.getTicker()).getCurrentPrice();
        double cost = marketOrder.getSize()*price;

        if(account.getAmount() < cost ){
            securityOrder.setStatus("Rejected!");
            throw new IllegalArgumentException(ExceptionUtil.buildMessage("Account contains insufficient funds"));
        }

        account.setAmount(account.getAmount() - cost);
        securityOrder.setPrice(price);
        securityOrder.setStatus("FILLED");

        accountRepo.save(account);

    }

    /**
     * Helper method to execute a sell order
     *
     * @param marketOrder user order
     * @param securityOrder to be saved in database
     * @param account account
     */
    protected void handleSellMarketOrder(MarketOrder marketOrder, SecurityOrder securityOrder, Account account) {
        Optional<Position> optionalPosition = positionRepo.findByAccountIdAndTicker(account.getId(),
            marketOrder.getTicker());

        if(!optionalPosition.isPresent()){
            securityOrder.setStatus("Rejected!");
            throw new EmptyResultDataAccessException(1);
        }
        Long positionValue = optionalPosition.get().getPosition();

        if (positionValue < marketOrder.getSize()) {
            securityOrder.setStatus("REJECTED");
            throw new IllegalArgumentException("Insufficient position");
        }

        double price = quoteService.findQuoteByTicker(marketOrder.getTicker()).getCurrentPrice();
        double proceeds = price * marketOrder.getSize();

        account.setAmount(account.getAmount() + proceeds);
        accountRepo.save(account);

        securityOrder.setPrice(price);
        securityOrder.setStatus("FILLED");

    }

}
