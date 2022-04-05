package proj2.src.patel.registry;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Transaction implements Serializable {
    String currency_code;
    double price;
    double qty;
    String transactionType; // Buy(B) or selling(S)
    LocalDateTime timestamp;

    public Transaction(String currency_code, double price, double qty, String transactionType) {
        this.currency_code = currency_code;
        this.price = price;
        this.qty = qty;
        this.transactionType = transactionType;
        this.timestamp = LocalDateTime.now();
    }

    public String toString() {
        return String.format("%s [%s] at %s %.8f units at price $%.2f ea = $%.2f",
                (transactionType.equalsIgnoreCase("Bought") ? "Bought" : "Sold"), currency_code, timestamp, qty, price,
                (qty * price));
    }
}
