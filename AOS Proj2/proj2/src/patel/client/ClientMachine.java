package proj2.src.patel.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import javax.swing.text.html.parser.Entity;

import proj2.src.patel.registry.Client;
import proj2.src.patel.registry.Coin;
import proj2.src.patel.server.ServerRMIInterface;

public class ClientMachine {
    static Client CLIENT_OBJ;
    private static ServerRMIInterface MAIN_SERVER;

    public static void main(String args[]) {

        // Locate Remote registry
        Scanner s = new Scanner(System.in);
        String server_ip = "127.0.0.1:9101";// = s.nextLine();//
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
                Map<String, Client> clients = MAIN_SERVER.getClientList();
                System.out.print("Enter your id:pass or Exit: ");
                String idColPass = s.nextLine();

                if (idColPass.equalsIgnoreCase("EXIT")) {
                    break;
                }
                String client_id = "", client_pass = "";
                try {
                    client_id = idColPass.split(":")[0];
                    client_pass = idColPass.split(":")[1];
                } catch (Exception e) {
                    System.err.println("Invalid input");
                    continue;
                }

                if (clients.containsKey(client_id)) {
                    // look for your user id
                    CLIENT_OBJ = clients.get(client_id);
                    if (!CLIENT_OBJ.checkLogin(client_id, client_pass)) {
                        System.err.println("Invalid password credentials");
                        CLIENT_OBJ = null;
                    }
                } else {
                    System.err.println("Invalid login credentials");
                    continue;
                }

                if (CLIENT_OBJ != null) {
                    // Menu loop begin
                    while (true) {
                        // refresh data
                        CLIENT_OBJ = MAIN_SERVER.getClientList().get(client_id);

                        System.out.println(
                                "[1] Check Wallet\n[2] Check Coins\n[3] Buy Coin\n[4] Sell Coin\n[5] Log Out\nChoice: ");
                        choice = s.nextLine();

                        if (choice.trim().equalsIgnoreCase("1")) {
                            // [1] Check Wallet
                            System.out.println(CLIENT_OBJ.toString());
                        } else if (choice.trim().equalsIgnoreCase("2")) {
                            // [2] Check Coins
                            for (Entry<String, Coin> coinImpl : MAIN_SERVER.getCoinList().entrySet()) {
                                System.out.println(
                                        coinImpl.getValue().printDetails());
                            }
                        } else if (choice.trim().equalsIgnoreCase("3")) {
                            // [3] Buy Coin
                            System.out.println(MAIN_SERVER.performPurchase("sp", true, "BTC", 10));
                        } else if (choice.trim().equalsIgnoreCase("4")) {
                            // [4] Sell Coin
                        } else if (choice.trim().equalsIgnoreCase("5")) {
                            // [5] Log Out
                            break;
                        } else {
                            System.out.println("Invalid Input try again");
                        }
                    } // menu loop end
                }
            } // logon loop ends
              // getting reference of exported coin objects

        } catch (NumberFormatException | RemoteException | NotBoundException e) {
            System.err.println("Client Error: " + e.toString());
        }
        s.close();
    }
}
