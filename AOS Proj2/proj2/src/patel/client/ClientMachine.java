package proj2.src.patel.client;

//*********************************************************
//**** Name: Swapnil Patel. Id: 1966690. Course: AOS
//**** Project-2, Date: 04/06/2022
//*********************************************************

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Scanner;

import proj2.src.patel.registry.Client;
import proj2.src.patel.registry.CoinRMIInterface;
import proj2.src.patel.registry.ServerRMIInterface;

public class ClientMachine {
    private static ServerRMIInterface MAIN_SERVER;

    public static void main(String args[]) {

        // Locate Remote registry
        Scanner s = new Scanner(System.in);
        System.out.print("Input server ip:port ");
        String server_ip = s.nextLine();// ="127.0.0.1:911";//
        try {
            Registry registry = LocateRegistry.getRegistry(server_ip.split(":")[0],
                    Integer.parseInt(server_ip.split(":")[1]));
            System.out.println("Connected to server");
            String choice;

            // get client manager instance
            MAIN_SERVER = (ServerRMIInterface) registry.lookup("server");
            // Login loop
            while (true) {
                // refresh clients data
                System.out.print("Enter your id:pass or Exit: ");
                String idColPass = s.nextLine();

                if (idColPass.equalsIgnoreCase("EXIT")) {
                    break;
                }
                String client_id = "", client_pass = "";

                Client CLIENT_STUB;
                try {
                    client_id = idColPass.split(":")[0];
                    client_pass = idColPass.split(":")[1];
                    CLIENT_STUB = MAIN_SERVER.getClient(client_id, client_pass);
                } catch (Exception e) {
                    System.err.println("Invalid input");
                    continue;
                }
                // Menu loop begin
                while (true) {
                    // refresh data
                    CLIENT_STUB = MAIN_SERVER.getClient(client_id, client_pass);
                    // server has removed client
                    if (CLIENT_STUB == null) {
                        System.err.println("Invalid input");
                        break;
                    }

                    System.out.print(
                            "[1] Check Wallet\n[2] Check Coins\n[3] Buy Coin\n[4] Sell Coin\n[5] Refresh\n[6] Log Out\nChoice: ");
                    choice = s.nextLine();

                    if (choice.trim().equalsIgnoreCase("1")) {
                        // [1] Check Wallet
                        System.out.println(CLIENT_STUB.toString());
                    } else if (choice.trim().equalsIgnoreCase("2")) {
                        // list all the server registry but server key
                        System.out.println("Coin list:");
                        for (String coin_key : Arrays.stream(registry.list()).filter(x -> !x.equalsIgnoreCase("server"))
                                .toArray(String[]::new)) {
                            CoinRMIInterface COIN_STUB = (CoinRMIInterface) registry.lookup(coin_key);
                            System.out.println(COIN_STUB.printDetails());
                        }
                    } else if (choice.trim().equalsIgnoreCase("3")) {
                        // [3] Buy Coin
                        System.out.print("Currency code to purchase ");
                        String cc = s.nextLine();
                        System.out.print("Enter amount in ($): ");
                        double amt = Double.parseDouble(s.nextLine());
                        System.out.println(MAIN_SERVER.performPurchase(client_id, true, cc, amt));
                    } else if (choice.trim().equalsIgnoreCase("4")) {
                        // [4] Sell Coin
                        System.out.print("Currency code to sell ");
                        String cc = s.nextLine();
                        System.out.print("Enter quantity of count to be sold: ");
                        double qty = Double.parseDouble(s.nextLine());
                        System.out.println(MAIN_SERVER.performPurchase(client_id, false, cc, qty));
                    } else if (choice.trim().equalsIgnoreCase("5")) {
                        // [5] Refresh
                        break;
                    } else if (choice.trim().equalsIgnoreCase("6")) {
                        // [6] Log Out
                        break;
                    } else {
                        System.out.println("Invalid Input try again");
                    }
                } // menu loop end
            } // logon loop ends
              // getting reference of exported coin objects

        } catch (NumberFormatException | RemoteException | NotBoundException e) {
            System.err.println("Client Error: " + e.toString());
        }
        s.close();
    }
}
