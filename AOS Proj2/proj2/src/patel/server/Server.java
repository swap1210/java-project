package proj2.src.patel.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

//*********************************************************
//**** Name: Swapnil Patel. Id: 1966690. Course: AOS
//**** Project-2, Date: 04/06/2022
//*********************************************************

import proj2.src.patel.registry.Client;
import proj2.src.patel.registry.ClientMenuManager;
import proj2.src.patel.registry.Coin;
import proj2.src.patel.registry.CoinMenuManager;
import proj2.src.patel.registry.ServerRMIInterface;
import proj2.src.patel.registry.Transaction;

public class Server implements ServerRMIInterface {

    ClientMenuManager clientManager = new ClientMenuManager();
    CoinMenuManager coinManager = new CoinMenuManager();
    Registry registry;

    public Server() {
    }

    public void main_menu(Scanner server_scan) {

        // System.setProperty("java.rmi.server.hostname", "127.0.0.1");
        try {
            System.out.print("Input server port no. ");
            int server_port = Integer.parseInt(server_scan.nextLine());
            registry = LocateRegistry.createRegistry(server_port);
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

        // check coin code
        Coin coin = coinManager.coin_list.get(currency_code);
        if (coin == null) {
            return "Invalid coin code!";
        }
        // if buying
        double calc_buying_qty = buying ? amt_or_qty / coin.opening_price : 0;
        // if selling fix the selling price
        double calc_selling_amt = !buying ? (amt_or_qty * coin.opening_price) : 0;

        // check volume avaiable if buying
        if (buying && calc_buying_qty > coin.trading_volume) {
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
        client.wallet.transactions.addFirst(new Transaction(currency_code, coin.opening_price,
                (buying ? calc_buying_qty : amt_or_qty), (buying ? "Bought" : "Sold")));

        // update coin volume
        if (buying) {
            coin.trading_volume = (coin.trading_volume - calc_buying_qty);
        } else {
            coin.trading_volume = (coin.trading_volume + amt_or_qty);
        }

        // save coin state
        coinManager.saveToFile();

        // save client state
        clientManager.saveToFile();
        System.out.println("here");
        return "TRANSACTION SUCCESS\nUser: " + client.name + "\n" + client.wallet.transactions.getFirst().toString();
    }

    // RMI to get client list
    @Override
    public Client getClient(String client_id, String client_pass) throws RemoteException {
        Client CLIENT_OBJ = this.clientManager.clients.get(client_id);
        // look for your user id and pass make returning object null if invalid login
        if (!CLIENT_OBJ.checkLogin(client_id, client_pass)) {
            CLIENT_OBJ = null;
        }

        return CLIENT_OBJ;
    }

}
