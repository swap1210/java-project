package proj2.src.patel.registry;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.time.LocalDateTime;

public class Coin implements Serializable {
    String name;
    final String currency_code;
    private String description;
    private double trading_volume;
    private double opening_price;
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
                "| %3s | %8s |\n%s\n  Market Cap $ %.2f\n  Trading Volume $ %.8f\n  Opening Price $ %.2f\n  Last Updated %s",
                currency_code, name, description, (trading_volume * opening_price), trading_volume, opening_price,
                timestamp.toString());
    }

    public String getName() {
        return name;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public String getDescription() {
        return description;
    }

    public double getMarket_cap() {
        return trading_volume * opening_price;
    }

    public double getTrading_volume() {
        return trading_volume;
    }

    public void setTrading_volume(double trading_volume) {
        this.trading_volume = trading_volume;
    }

    public double getOpening_price() {
        return opening_price;
    }

    public void setOpening_price(double opening_price) {
        this.opening_price = opening_price;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
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
