package tech.cassandre.trading.bot.service.intern;

import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.util.base.BaseService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.math.RoundingMode.HALF_UP;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;

/**
 * Position service implementation.
 */
public class PositionServiceImplementation extends BaseService implements PositionService {

    /** List of positions. */
    private final Map<Long, PositionDTO> positions = new LinkedHashMap<>();

    /** Trade service. */
    private final TradeService tradeService;

    /** Position repository. */
    private final PositionRepository positionRepository;

    /**
     * Constructor.
     *
     * @param newTradeService       trade service
     * @param newPositionRepository position repository
     */
    public PositionServiceImplementation(final TradeService newTradeService,
                                         final PositionRepository newPositionRepository) {
        this.tradeService = newTradeService;
        this.positionRepository = newPositionRepository;
    }

    @Override
    public final Set<PositionDTO> getPositions() {
        getLogger().debug("PositionService - Retrieving all positions");
        return new LinkedHashSet<>(positions.values());
    }

    @Override
    public final Optional<PositionDTO> getPositionById(final long id) {
        getLogger().debug("PositionService - Retrieving position {}", id);
        return Optional.ofNullable(positions.get(id));
    }

    @Override
    public final PositionCreationResultDTO createPosition(final CurrencyPairDTO currencyPair, final BigDecimal amount, final PositionRulesDTO rules) {
        // Trying to create an order.
        getLogger().debug("PositionService - Creating a position for {} on {} with the rules : {}", amount, currencyPair, rules);
        final OrderCreationResultDTO orderCreationResult = tradeService.createBuyMarketOrder(currencyPair, amount);
        // If it works, create the position.
        if (orderCreationResult.isSuccessful()) {
            // =========================================================================================================
            // Creates the position in database.
            Position position = new Position();
            position.setStatus(OPENING.toString());
            if (rules.isStopGainPercentageSet()) {
                position.setStopGainPercentageRule(rules.getStopGainPercentage());
            }
            if (rules.isStopLossPercentageSet()) {
                position.setStopLossPercentageRule(rules.getStopLossPercentage());
            }
            position.setOpenOrderId(orderCreationResult.getOrderId());
            position = positionRepository.save(position);
            // =========================================================================================================

            // =========================================================================================================
            // Creates the position dto.
            PositionDTO p = new PositionDTO(position.getId(), currencyPair, amount, orderCreationResult.getOrderId(), rules);
            positions.put(p.getId(), p);
            getLogger().debug("PositionService - Position {} opened with order {}", p.getId(), orderCreationResult.getOrderId());

            // =========================================================================================================
            // Creates the result.
            return new PositionCreationResultDTO(p.getId(), orderCreationResult.getOrderId());
        } else {
            getLogger().error("PositionService - Position creation failure : {}", orderCreationResult.getErrorMessage());
            // If it doesn't work, returns the error.
            return new PositionCreationResultDTO(orderCreationResult.getErrorMessage(), orderCreationResult.getException());
        }
    }

    @Override
    public final void tickerUpdate(final TickerDTO ticker) {
        // With the ticker received, we check for every position, if it should be closed.
        positions.values().stream()
                .filter(p -> p.getStatus().equals(OPENED))
                .filter(p -> p.getCurrencyPair() != null)
                .filter(p -> p.getCurrencyPair().equals(ticker.getCurrencyPair()))
                .filter(p -> p.shouldBeClosed(ticker))
                .forEach(p -> {
                    final OrderCreationResultDTO orderCreationResult = tradeService.createSellMarketOrder(ticker.getCurrencyPair(), p.getAmount());
                    if (orderCreationResult.isSuccessful()) {
                        p.setCloseOrderId(orderCreationResult.getOrderId());
                        getLogger().debug("PositionService - Position {} closed with order {}", p.getId(), orderCreationResult.getOrderId());
                    }
                });
    }

    @Override
    public final void tradeUpdate(final TradeDTO trade) {
        positions.values().forEach(p -> p.tradeUpdate(trade));
    }

    @Override
    public final HashMap<CurrencyDTO, GainDTO> getGains() {
        HashMap<CurrencyDTO, BigDecimal> totalBought = new LinkedHashMap<>();
        HashMap<CurrencyDTO, BigDecimal> totalSold = new LinkedHashMap<>();
        HashMap<CurrencyDTO, BigDecimal> totalFees = new LinkedHashMap<>();
        HashMap<CurrencyDTO, GainDTO> gains = new LinkedHashMap<>();

        // We calculate, by currency, the amount bought & sold.
        positions.values()
                .stream()
                .filter(p -> CLOSED.equals(p.getStatus()))
                .forEach(p -> {
                    // We retrieve the currency and initiate the maps if they are empty
                    CurrencyDTO currency = p.getCurrencyPair().getQuoteCurrency();
                    gains.putIfAbsent(currency, null);
                    totalBought.putIfAbsent(currency, BigDecimal.ZERO);
                    totalSold.putIfAbsent(currency, BigDecimal.ZERO);
                    totalFees.putIfAbsent(currency, BigDecimal.ZERO);

                    // We calculate the amounts bought and amount sold..
                    final BigDecimal bought = p.getOpenTrades()
                            .stream()
                            .map(t -> t.getOriginalAmount().multiply(t.getPrice()))
                            .reduce(totalBought.get(currency), BigDecimal::add);
                    totalBought.put(currency, bought);
                    final BigDecimal sold = p.getCloseTrades()
                            .stream()
                            .map(t -> t.getOriginalAmount().multiply(t.getPrice()))
                            .reduce(totalSold.get(currency), BigDecimal::add);
                    totalSold.put(currency, sold);
                    final BigDecimal fees = p.getTrades()
                            .stream()
                            .map(t -> t.getFee().getValue())
                            .reduce(totalFees.get(currency), BigDecimal::add);
                    totalFees.put(currency, fees);
                });

        gains.keySet()
                .forEach(currency -> {
                    // We make the calculation.
                    BigDecimal bought = totalBought.get(currency);
                    BigDecimal sold = totalSold.get(currency);
                    BigDecimal fees = totalFees.get(currency);
                    BigDecimal gainAmount = sold.subtract(bought);
                    BigDecimal gainPercentage = ((sold.subtract(bought)).divide(bought, HALF_UP)).multiply(new BigDecimal("100"));

                    System.out.println("==> " + fees);

                    GainDTO g = new GainDTO(gainPercentage.setScale(2, HALF_UP).doubleValue(),
                            new CurrencyAmountDTO(gainAmount, currency),
                            new CurrencyAmountDTO(fees, currency));
                    gains.put(currency, g);
                });
        return gains;
    }

    @Override
    public final void restorePosition(final PositionDTO position) {
        positions.put(position.getId(), position);
    }

    @Override
    public final void backupPosition(final PositionDTO position) {
        Optional<Position> p = positionRepository.findById(position.getId());
        if (p.isPresent()) {
            p.get().setId(position.getId());
            p.get().setStatus(position.getStatus().toString());
            if (position.getRules().isStopGainPercentageSet()) {
                p.get().setStopGainPercentageRule(position.getRules().getStopGainPercentage());
            }
            if (position.getRules().isStopLossPercentageSet()) {
                p.get().setStopLossPercentageRule(position.getRules().getStopLossPercentage());
            }
            position.getTrades().forEach(t -> p.get().getTrades().add(t.getId()));
//            position.getOpenTrades().forEach((s, t) -> p.get().getTrades().add(s));
//            position.getCloseTrades().forEach((s, t) -> p.get().getTrades().add(s));
            p.get().setOpenOrderId(position.getOpenOrderId());
            p.get().setCloseOrderId(position.getCloseOrderId());
            p.get().setLowestPrice(position.getLowestPrice());
            p.get().setHighestPrice(position.getHighestPrice());
            positionRepository.save(p.get());
        } else {
            // Position was not found.
            getLogger().error("Position {} was not saved because it was not found in database", position.getId());
        }
    }

}
