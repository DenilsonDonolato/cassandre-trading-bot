package tech.cassandre.trading.bot.test.util.junit.configuration;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

import static tech.cassandre.trading.bot.test.util.strategies.InvalidStrategy.PARAMETER_INVALID_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.strategies.NoTradingAccountStrategy.PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy.PARAMETER_TESTABLE_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.util.parameters.DatabaseParameters.Datasource.PARAMETER_DATABASE_DATASOURCE_DRIVER_CLASS_NAME;
import static tech.cassandre.trading.bot.util.parameters.DatabaseParameters.Datasource.PARAMETER_DATABASE_DATASOURCE_PASSWORD;
import static tech.cassandre.trading.bot.util.parameters.DatabaseParameters.Datasource.PARAMETER_DATABASE_DATASOURCE_URL;
import static tech.cassandre.trading.bot.util.parameters.DatabaseParameters.Datasource.PARAMETER_DATABASE_DATASOURCE_USERNAME;
import static tech.cassandre.trading.bot.util.parameters.DatabaseParameters.PARAMETER_DATABASE_TABLE_PREFIX;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_EXCHANGE_DRY;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_EXCHANGE_SANDBOX;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_EXCHANGE_KEY;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_EXCHANGE_NAME;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_EXCHANGE_PASSPHRASE;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_EXCHANGE_SECRET;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_EXCHANGE_USERNAME;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_EXCHANGE_RATE_ACCOUNT;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_EXCHANGE_RATE_TICKER;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_EXCHANGE_RATE_TRADE;

/**
 * Configuration extension - set and clear system properties.
 */
@NotThreadSafe // System properties are JVM-global, so don't run tests in parallel with this rule.
public class ConfigurationExtension implements BeforeAllCallback, AfterAllCallback {

    /** Invalid strategy enabled parameter default value. */
    public static final String PARAMETER_INVALID_STRATEGY_DEFAULT_VALUE = "false";

    /** Testable strategy enabled parameter default value. */
    public static final String PARAMETER_TESTABLE_STRATEGY_DEFAULT_VALUE = "true";

    /** Testable ta4j strategy enabled parameter default value. */
    public static final String PARAMETER_TESTABLE_TA4J_STRATEGY_DEFAULT_VALUE = "false";

    /** Strategy without existing account enabled parameter default value. */
    public static final String PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_DEFAULT_VALUE = "false";

    /** Exchange name parameter. */
    public static final String PARAMETER_NAME_DEFAULT_VALUE = "kucoin";

    /** Sandbox parameter. */
    public static final String PARAMETER_SANDBOX_DEFAULT_VALUE = "true";

    /** Dry parameter. */
    public static final String PARAMETER_DRY_DEFAULT_VALUE = "false";

    /** Username parameter. */
    public static final String PARAMETER_USERNAME_DEFAULT_VALUE = "cassandre.crypto.bot@gmail.com";

    /** Passphrase parameter. */
    public static final String PARAMETER_PASSPHRASE_DEFAULT_VALUE = "cassandre";

    /** Key parameter. */
    public static final String PARAMETER_KEY_DEFAULT_VALUE = "5df8eea30092f40009cb3c6a";

    /** Secret parameter. */
    public static final String PARAMETER_SECRET_DEFAULT_VALUE = "5f6e91e0-796b-4947-b75e-eaa5c06b6bed";

    /** Rate for account parameter. */
    public static final String PARAMETER_RATE_ACCOUNT_DEFAULT_VALUE = "100";

    /** Rate for ticker parameter. */
    public static final String PARAMETER_RATE_TICKER_DEFAULT_VALUE = "101";

    /** Rate for trade parameter. */
    public static final String PARAMETER_RATE_TRADE_DEFAULT_VALUE = "102";

    /** Database datasource driver. */
    public static final String PARAMETER_DATABASE_DATASOURCE_DRIVER_CLASS_NAME_DEFAULT_VALUE = "org.hsqldb.jdbc.JDBCDriver";

    /** Database datasource url. */
    public static final String PARAMETER_DATABASE_DATASOURCE_URL_DEFAULT_VALUE = "jdbc:hsqldb:mem:cassandre-database;shutdown=true";

    /** Database datasource username. */
    public static final String PARAMETER_DATABASE_DATASOURCE_USERNAME_DEFAULT_VALUE = "sa";

    /** Database datasource password. */
    public static final String PARAMETER_DATABASE_DATASOURCE_PASSWORD_DEFAULT_VALUE = "";

    /** Table prefix. */
    public static final String PARAMETER_DATABASE_TABLE_PREFIX_DEFAULT_VALUE = "MY_STRATEGY_";

    @Override
    public void beforeAll(ExtensionContext context) {
        // Set default values.

        // Exchange parameters.
        System.setProperty(PARAMETER_EXCHANGE_NAME, PARAMETER_NAME_DEFAULT_VALUE);                                                          // Kucoin
        System.setProperty(PARAMETER_EXCHANGE_SANDBOX, PARAMETER_SANDBOX_DEFAULT_VALUE);                                                    // true
        System.setProperty(PARAMETER_EXCHANGE_DRY, PARAMETER_DRY_DEFAULT_VALUE);                                                            // false
        System.setProperty(PARAMETER_EXCHANGE_USERNAME, PARAMETER_USERNAME_DEFAULT_VALUE);                                                  // cassandre.crypto.bot@gmail.com
        System.setProperty(PARAMETER_EXCHANGE_PASSPHRASE, PARAMETER_PASSPHRASE_DEFAULT_VALUE);                                              // cassandre
        System.setProperty(PARAMETER_EXCHANGE_KEY, PARAMETER_KEY_DEFAULT_VALUE);                                                            // 5df8eea30092f40009cb3c6a
        System.setProperty(PARAMETER_EXCHANGE_SECRET, PARAMETER_SECRET_DEFAULT_VALUE);                                                      // 5f6e91e0-796b-4947-b75e-eaa5c06b6bed
        System.setProperty(PARAMETER_EXCHANGE_RATE_ACCOUNT, PARAMETER_RATE_ACCOUNT_DEFAULT_VALUE);                                          // 100
        System.setProperty(PARAMETER_EXCHANGE_RATE_TICKER, PARAMETER_RATE_TICKER_DEFAULT_VALUE);                                            // 101
        System.setProperty(PARAMETER_EXCHANGE_RATE_TRADE, PARAMETER_RATE_TRADE_DEFAULT_VALUE);                                              // 102
        // Database parameters.
        System.setProperty(PARAMETER_DATABASE_DATASOURCE_DRIVER_CLASS_NAME, PARAMETER_DATABASE_DATASOURCE_DRIVER_CLASS_NAME_DEFAULT_VALUE); // org.hsqldb.jdbc.JDBCDriver
        System.setProperty(PARAMETER_DATABASE_DATASOURCE_URL, PARAMETER_DATABASE_DATASOURCE_URL_DEFAULT_VALUE);                             // jdbc:hsqldb:mem:cassandre-database;shutdown=true
        System.setProperty(PARAMETER_DATABASE_DATASOURCE_USERNAME, PARAMETER_DATABASE_DATASOURCE_USERNAME_DEFAULT_VALUE);                   // sa
        System.setProperty(PARAMETER_DATABASE_DATASOURCE_PASSWORD, PARAMETER_DATABASE_DATASOURCE_PASSWORD_DEFAULT_VALUE);                   // empty
        System.setProperty(PARAMETER_DATABASE_TABLE_PREFIX, PARAMETER_DATABASE_TABLE_PREFIX_DEFAULT_VALUE);                                 // MY_STRATEGY_
        // Strategies configuration.
        System.setProperty(PARAMETER_INVALID_STRATEGY_ENABLED, PARAMETER_INVALID_STRATEGY_DEFAULT_VALUE);                                   // false
        System.setProperty(PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED, PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_DEFAULT_VALUE);             // false
        System.setProperty(PARAMETER_TESTABLE_STRATEGY_ENABLED, PARAMETER_TESTABLE_STRATEGY_DEFAULT_VALUE);                                 // true
        System.setProperty(PARAMETER_TESTABLE_TA4J_STRATEGY_DEFAULT_VALUE, PARAMETER_TESTABLE_TA4J_STRATEGY_DEFAULT_VALUE);                 // false
        // Spring parameters.
        System.setProperty("spring.jpa.hibernate.ddl-auto", "validate");

        // Retrieve all the properties set by the annotation.
        final Optional<Class<?>> testClass = context.getTestClass();
        if (testClass.isPresent()) {
            final Configuration configuration = testClass.get().getAnnotation(Configuration.class);
            final Iterator<Property> systemPropertyIterator = Arrays.stream(configuration.value()).iterator();
            systemPropertyIterator.forEachRemaining(s -> {
                if (s.value().equals("")) {
                    System.clearProperty(s.key());
                } else {
                    System.setProperty(s.key(), s.value());
                }
            });
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        // Reset values.

        // Exchange parameters.
        System.clearProperty(PARAMETER_EXCHANGE_NAME);
        System.clearProperty(PARAMETER_EXCHANGE_SANDBOX);
        System.clearProperty(PARAMETER_EXCHANGE_DRY);
        System.clearProperty(PARAMETER_EXCHANGE_USERNAME);
        System.clearProperty(PARAMETER_EXCHANGE_PASSPHRASE);
        System.clearProperty(PARAMETER_EXCHANGE_KEY);
        System.clearProperty(PARAMETER_EXCHANGE_SECRET);
        System.clearProperty(PARAMETER_EXCHANGE_RATE_ACCOUNT);
        System.clearProperty(PARAMETER_EXCHANGE_RATE_TICKER);
        System.clearProperty(PARAMETER_EXCHANGE_RATE_TRADE);
        // Database parameters.
        System.clearProperty(PARAMETER_DATABASE_DATASOURCE_DRIVER_CLASS_NAME);
        System.clearProperty(PARAMETER_DATABASE_DATASOURCE_URL);
        System.clearProperty(PARAMETER_DATABASE_DATASOURCE_USERNAME);
        System.clearProperty(PARAMETER_DATABASE_DATASOURCE_PASSWORD);
        System.clearProperty(PARAMETER_DATABASE_TABLE_PREFIX);
        // Strategies configuration.
        System.clearProperty(PARAMETER_INVALID_STRATEGY_ENABLED);
        System.clearProperty(PARAMETER_TESTABLE_STRATEGY_ENABLED);
        System.clearProperty(PARAMETER_TESTABLE_TA4J_STRATEGY_DEFAULT_VALUE);
        System.clearProperty(PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED);

        // Remove all the properties set for this method.
        final Optional<Class<?>> testClass = context.getTestClass();
        if (testClass.isPresent()) {
            final Configuration configuration = testClass.get().getAnnotation(Configuration.class);
            final Iterator<Property> systemPropertyIterator = Arrays.stream(configuration.value()).iterator();
            systemPropertyIterator.forEachRemaining(s -> System.clearProperty(s.key()));
        }
    }

}
