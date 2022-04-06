package proj2.src.patel.registry;

//*********************************************************
//**** Name: Swapnil Patel. Id: 1966690. Course: AOS
//**** Project-2, Date: 04/06/2022
//*********************************************************

import java.io.Serializable;
import java.time.LocalDateTime;

public class Coin implements Serializable, CoinRMIInterface {
    String name;
    final String currency_code;
    String description;
    public double trading_volume;
    public double opening_price;
    LocalDateTime timestamp;

    public Coin(String name, String currency_code, String description, double trading_volume,
            double opening_price) {
        this.name = name;
        this.currency_code = currency_code;
        this.description = description;
        this.trading_volume = trading_volume;
        this.opening_price = opening_price;
        this.timestamp = LocalDateTime.now();
    }

    public String printDetails() {
        return String.format(
                "| %3s | %8s |\n%s\n  Market Cap $ %.2f\n  Trading Volume %.8f units\n  Opening Price $ %.2f per unit\n  Last Updated %s",
                currency_code, name, description, (trading_volume * opening_price), trading_volume, opening_price,
                timestamp.toString());
    }

    public double getMarket_cap() {
        return trading_volume * opening_price;
    }

    public void editCoin(String name, String description, double trading_volume,
            double opening_price) {
        this.name = name;
        this.description = description;
        this.trading_volume = trading_volume;
        this.opening_price = opening_price;
        this.timestamp = LocalDateTime.now();
    }

}
