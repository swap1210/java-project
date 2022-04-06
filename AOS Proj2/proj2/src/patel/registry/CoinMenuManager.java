package proj2.src.patel.registry;

//*********************************************************
//**** Name: Swapnil Patel. Id: 1966690. Course: AOS
//**** Project-2, Date: 04/06/2022
//*********************************************************

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

public class CoinMenuManager implements MenuManager {

    public Map<String, Coin> coin_list;
    Registry registry;
    Scanner s;

    public CoinMenuManager(Registry registry, Scanner s) {
        this.registry = registry;
        this.s = s;

        // try to read coins list from file
        try {
            ObjectInputStream ois = null;
            ois = new ObjectInputStream(new FileInputStream(new File("./proj2/assets/coins_data.obj")));
            coin_list = (Map<String, Coin>) ois.readObject();
            for (Entry<String, Coin> coin : coin_list.entrySet()) {
                this.registry.bind(coin.getKey(), coin.getValue());
            }
            ois.close();
        } catch (Exception e) {
            System.err.println("Couldn't read coin file. " + e.getMessage());
            coin_list = new HashMap<String, Coin>();
        }
    }

    public CoinMenuManager() {
    }

    public void menu() {

        // if load from file modify scanner
        if (DEBUG_FLAG) {
            try {
                s = new Scanner(new File("./proj2/assets/coin_input.txt"));
            } catch (FileNotFoundException e) {
                System.err.println("Error " + e.getMessage());
            }
        }
        String choice;
        while (true) {

            boolean needFileEdit = false;
            try {
                System.out.print(
                        "[1] List Coins\n[2] Create New Coin\n[3] Edit Coin\n[4] Remove Coin\n[5] Back\nChoice: ");
                choice = s.nextLine();

                if (choice.trim().equalsIgnoreCase("1")) {
                    // [1] List of Coins
                    this.listCoins();
                } else if (choice.trim().equalsIgnoreCase("2")) {
                    // [2] Create New Coin
                    this.createCoin();
                    needFileEdit = true;
                } else if (choice.trim().equalsIgnoreCase("3")) {
                    // [3] Edit Coin
                    this.editCoin();
                    needFileEdit = true;
                } else if (choice.trim().equalsIgnoreCase("4")) {
                    // [4] Remove Coin
                    this.removeCoin();
                    needFileEdit = true;
                } else if (choice.trim().equalsIgnoreCase("5")) {
                    // [5] Back
                    break;
                } else {
                    System.out.println("Invalid Input try again");
                }

                if (needFileEdit) {
                    saveToFile();
                }
            } catch (RemoteException r) {
                System.err.println("Error " + r.getMessage());
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    private void removeCoin() throws Exception {
        System.out.print("Coin code to be deleted: ");
        String coin_code = s.nextLine();
        // if exist in coin list remove from list and registry
        if (this.coin_list != null && this.coin_list.containsKey(coin_code)) {
            System.out.print("Confirm deleting coin:[" + coin_code + "] (Y/N) : ");
            String confirm = s.nextLine();
            if (confirm.equalsIgnoreCase("Y")) {
                this.registry.unbind(coin_code);
                this.coin_list.remove(coin_code);
                System.out.println("Deleted client " + coin_code);
            }
        } else {
            throw new Exception("Invalid coin code");
        }
    }

    void createCoin() throws RemoteException, AlreadyBoundException {
        System.out.print("New Coin name: ");
        String name = s.nextLine();
        System.out.print("New Coin code: ");
        String currency_code = s.nextLine();
        System.out.print("New Coin description: ");
        String description = s.nextLine();
        System.out.print("New Coin trading volume: ");
        String trading_volume = s.nextLine();
        System.out.print("New Coin opening price: ");
        String opening_price = s.nextLine();
        Coin temp_coin = new Coin(name, currency_code,
                description, Double.parseDouble(trading_volume), Double.parseDouble(opening_price));

        // if exist in coin list remove from list and registry
        if (this.coin_list != null) {
            this.registry.bind(currency_code, temp_coin);
            this.coin_list.put(currency_code, temp_coin);
        } else {
            System.out.println("Coin list not initialized");
        }
    }

    private void editCoin() throws Exception {

        System.out.print("Currency code of coin to be edited: ");
        String currency_code = s.nextLine();
        System.out.println("If you don't want to change just press Enter");
        Coin edited_coin = coin_list.get(currency_code);
        if (edited_coin == null) {
            throw new Exception("No such coin found");
        }
        System.out.print("Change coin name (" + edited_coin.name + ") :");
        String c_name = s.nextLine();
        if (c_name.isBlank())
            c_name = edited_coin.name;
        System.out.print("Change coin description (" + edited_coin.description + ") :");
        String c_desc = s.nextLine();
        if (c_desc.isBlank())
            c_desc = edited_coin.description;
        System.out.print("Change coin price(" + edited_coin.opening_price + "): ");
        String temp = s.nextLine();
        if (temp.isBlank())
            temp = "0";
        double c_price = Double.parseDouble(temp);
        if (c_price == 0)
            c_price = edited_coin.opening_price;
        System.out.print("Change coin volume(" + edited_coin.trading_volume + "): ");
        temp = s.nextLine();
        if (temp.isBlank())
            temp = "0";
        double c_volume = Double.parseDouble(temp);
        if (c_volume == 0)
            c_volume = edited_coin.trading_volume;
        edited_coin.editCoin(c_name, c_desc, c_volume, c_price);
        this.registry.rebind(currency_code, edited_coin);
    }

    void listCoins() {
        System.out.println("Coins:");
        for (Entry<String, Coin> coinImpl : coin_list.entrySet()) {
            System.out.println(
                    coinImpl.getValue().printDetails());
        }

        if (coin_list.size() == 0) {
            System.out.println("--0 coins found.--");
        }
    }

    @Override
    public void saveToFile() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(new File("./proj2/assets/coins_data.obj")));
            oos.writeObject(coin_list);
            // oos.flush();
            oos.close();
            // System.out.println("Saved to file");
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
