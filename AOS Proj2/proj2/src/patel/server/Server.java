package proj2.src.patel.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import proj2.src.patel.registry.Client;
import proj2.src.patel.registry.ClientMenuManager;
import proj2.src.patel.registry.Coin;
import proj2.src.patel.registry.CoinMenuManager;
import proj2.src.patel.registry.Transaction;

public class Server implements ServerRMIInterface {

    ClientMenuManager clientManager = new ClientMenuManager();
    CoinMenuManager coinManager = new CoinMenuManager();
    Registry registry;

    public Server() {
        System.out.println("In server constructor");
    }

    public void main_menu(Scanner server_scan) {

        // System.setProperty("java.rmi.server.hostname", "127.0.0.1");
        try {
            registry = LocateRegistry.createRegistry(9101);
            clientManager = new ClientMenuManager(registry, server_scan);
            coinManager = new CoinMenuManager(registry, server_scan);
            // register for server operation
            ServerRMIInterface sri;
            sri = (ServerRMIInterface) UnicastRemoteObject.exportObject(this, 0);
            registry.rebind("server", sri);
            System.out.println("server Registration success");
        } catch (RemoteException e) {
            System.err.println("Error while registering " + e.getMessage());
        }
        String choice;
        while (true) {
            System.out.print(
                    "[1] Client Operations\n[2] Coin Operation\n[3] Exit\nChoice: ");
            choice = server_scan.nextLine();
            if (choice.trim().equalsIgnoreCase("1")) {
                // [1] Client Operations
                clientManager.menu();
            } else if (choice.trim().equalsIgnoreCase("2")) {
                // [2] Coin Operation
                coinManager.menu();
            } else if (choice.trim().equalsIgnoreCase("3")) {
                // [3] Exit
                server_scan.close();
                System.exit(0);
            } else {
                System.out.println("Invalid Input try again");
            }
        }
    }

    public synchronized String performPurchase(String user_id, boolean buying, String currency_code, double amt_or_qty)
            throws RemoteException {
        // amt_or_qty is amt in dollar when buying coin and qty of coin while selling
        // coin
        String temp = "";

        // check coin code
        Coin coin = coinManager.coin_list.get(currency_code);
        if (coin == null) {
            return "Invalid coin code!";
        }
        // if buying
        double calc_buying_qty = buying ? amt_or_qty / coin.getOpening_price() : 0;
        // if selling fix the selling price
        double calc_selling_amt = !buying ? (amt_or_qty * coin.getOpening_price()) : 0;

        // check volume avaiable if buying
        if (buying && calc_buying_qty > coin.getTrading_volume()) {
            return "Requested Coin Volume unavailable in the sever!";
        }

        // check if client exists
        Client client = clientManager.clients.get(user_id);
        if (client == null) {
            return "Invalid user id!";
        }
        // check client balance
        if (buying && amt_or_qty > client.wallet.balance) {
            return "Client purchase power exceeded!";
        }

        // update client balance
        if (buying) {
            client.wallet.balance -= amt_or_qty;
        } else {
            client.wallet.balance += calc_selling_amt;
        }

        // add/update coin to client if doesn't exists
        if (client.wallet.coins.containsKey(currency_code)) {
            double existing_qty = client.wallet.coins.get(currency_code);

            // if buying add to coin amt
            if (buying)
                client.wallet.coins.put(currency_code, existing_qty + calc_buying_qty);
            // if selling subtract from existing amt
            if (!buying)
                client.wallet.coins.put(currency_code, existing_qty - amt_or_qty);
        } else {
            // add new coin balance only if buying new coin
            client.wallet.coins.put(currency_code, calc_buying_qty);
        }

        // public Transaction(String currency_code, double price, double qty, String
        // transactionType)
        // update transaction list
        client.wallet.transactions.add(new Transaction(currency_code, coin.getOpening_price(),
                (buying ? calc_buying_qty : amt_or_qty), (buying ? "Bought" : "Sold")));

        // update coin volume
        if (buying) {
            coin.setTrading_volume(coin.getTrading_volume() + calc_buying_qty);
        } else {
            coin.setTrading_volume(coin.getTrading_volume() + amt_or_qty);
        }

        // save coin state
        coinManager.saveToFile();

        // save client state
        clientManager.saveToFile();
        return temp;
    }

    // RMI to get client list
    @Override
    public Map<String, Client> getClientList() throws RemoteException {
        return this.clientManager.clients;
    }

    @Override
    public Map<String, Coin> getCoinList() throws RemoteException {
        return this.coinManager.coin_list;
    }
}
