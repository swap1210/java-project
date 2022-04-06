package proj2.src.patel.registry;

//*********************************************************
//**** Name: Swapnil Patel. Id: 1966690. Course: AOS
//**** Project-2, Date: 04/06/2022
//*********************************************************

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

public class Wallet implements Serializable {

    public double balance;
    public Map<String, Double> coins;
    public LinkedList<Transaction> transactions;

    Wallet() {
        this.balance = 0;
        this.coins = new HashMap<String, Double>(); // just need to know the coin code and it's price
        this.transactions = new LinkedList<Transaction>();
    }

    @Override
    public String toString() {
        String temp = " Wallet:\n Purchase Power: $" + this.balance + "\n Coin:\n";

        for (Entry<String, Double> coin : this.coins.entrySet()) {
            temp += "  " + coin.getKey() + "= " + String.format("%,.8f", coin.getValue()) + "\n";
        }

        temp += " Transaction:\n";

        for (Object transaction : this.transactions.toArray()) {
            temp += "  " + ((Transaction) transaction).toString() + "\n";
        }
        return temp;
    }
}