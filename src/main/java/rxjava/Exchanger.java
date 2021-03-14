package rxjava;

import rxjava.reactive_mongo_driver.model.Currency;

public class Exchanger {
    private static final double EXCHANGE_RATE_USD_TO_RUB = 75;
    private static final double EXCHANGE_RATE_EURO_TO_RUB = 90;

    public static String showConverted(Currency currencyTo, double amount) {
        switch (currencyTo) {
            case EURO:
                return String.format("%.2f EURO", amount / EXCHANGE_RATE_EURO_TO_RUB);
            case USD:
                return String.format("%.2f USD", amount / EXCHANGE_RATE_USD_TO_RUB);
            default:
                return amount + " RUB";
        }
    }
}
