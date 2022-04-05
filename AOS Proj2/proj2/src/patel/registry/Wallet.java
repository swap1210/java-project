package proj2.src.patel.registry;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Wallet implements Serializable {

    public double balance;
    public Map<String, Double> coins;
    public List<Transaction> transactions;

    Wallet() {
        this.balance = 0;
        this.coins = new HashMap<String, Double>(); // just need to know the coin code and it's price
        this.transactions = new LinkedList<Transaction>();
    }

    @Override
    public String toString() {
        String temp = " Wallet:\n Purchase Power: $" + this.balance + "\n Coin:\n";

        for (Entry<String, Double> coin : this.coins.entrySet()) {
            temp += "  " + coin.getKey() + "= " + coin.getValue() + "\n";
        }

        temp += " Transaction:\n";

        for (Object transaction : this.transactions.toArray()) {
            temp += "  " + ((Transaction) transaction).toString() + "\n";
        }
        return temp;
    }
}