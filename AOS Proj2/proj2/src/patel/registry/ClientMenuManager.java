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
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

public class ClientMenuManager implements MenuManager {
    public Map<String, Client> clients = null;
    Registry registry;
    Scanner s;

    public ClientMenuManager(Registry registry, Scanner s) {
        this.registry = registry;
        this.s = s;
        // try to read clients list from file
        try {
            ObjectInputStream ois = null;
            ois = new ObjectInputStream(new FileInputStream(new File("./proj2/assets/clients_data.obj")));
            clients = (Map<String, Client>) ois.readObject();
            // listClients();
            ois.close();
        } catch (Exception e) {
            System.err.println("No clients found");
            clients = new HashMap<>();
        }
    }

    public ClientMenuManager() {
    }

    public void menu() {

        // if load from file modify scanner
        if (DEBUG_FLAG) {
            try {
                System.out.println(System.getProperty("user.dir"));
                s = new Scanner(new File("./proj2/assets/client_input.txt"));
            } catch (FileNotFoundException e) {
                System.err.println("File Not Found");
            }
        }

        String choice;
        while (true) {
            System.out.print(
                    "[1] List of Clients\n[2] Create New Client\n[3] Edit Client\n[4] Delete Client\n[5] Reset file\n[6] Back\nChoice: ");
            choice = s.nextLine();
            boolean needFileEdit = false;
            if (choice.trim().equalsIgnoreCase("1")) {
                // [1] List of Clients
                listClients();
            } else if (choice.trim().equalsIgnoreCase("2")) {
                // [2] Create New Client
                createClient();
                needFileEdit = true;
            } else if (choice.trim().equalsIgnoreCase("3")) {
                // [3] Edit Client
                editClient();
                needFileEdit = true;
            } else if (choice.trim().equalsIgnoreCase("4")) {
                // [4] Delete Client
                deleteClient();
                needFileEdit = true;
            } else if (choice.trim().equalsIgnoreCase("5")) {
                // [5] Reset file
                clients = new HashMap<>();
                needFileEdit = true;
            } else if (choice.trim().equalsIgnoreCase("6")) {
                // [8] Back
                break;
            } else {
                System.out.println("Invalid Input try again");
            }

            if (needFileEdit) {
                saveToFile();
            }
        } // choice loop end
    }

    private void deleteClient() {
        System.out.print("Id of client to be deleted: ");
        String c_id = s.nextLine();
        System.out.print("Confirm deleting client:[" + c_id + "] (Y/N) : ");
        String confirm = s.nextLine();
        if (confirm.equalsIgnoreCase("Y")) {
            clients.remove(c_id);
            System.out.println("Deleted client " + c_id);
        }
    }

    private void editClient() {
        System.out.print("Id of client to be edited: ");
        String c_id = s.nextLine();
        System.out.println("If you don't want to change just press Enter");
        Client edited_client = clients.get(c_id);
        System.out.print("Change client name (" + edited_client.name + ") :");
        String c_name = s.nextLine();
        if (c_name.isBlank())
            c_name = edited_client.name;
        System.out.print("New client password [*required]: ");
        String c_pass = s.nextLine();
        System.out.print("New client purchasing power ($" + edited_client.wallet.balance + ") :");
        String c_purchase_power = s.nextLine();
        if (c_purchase_power.isBlank())
            c_purchase_power = edited_client.wallet.balance + "";
        edited_client.editClient(c_name, c_pass.toCharArray(),
                Double.parseDouble(c_purchase_power));
    }

    void createClient() {
        System.out.print("New client name: ");
        String c_name = s.nextLine();
        System.out.print("New client id: ");
        String c_id = s.nextLine();
        System.out.print("New client password: ");
        String c_pass = s.nextLine();
        System.out.print("New client purchasing power: ");
        String c_purchase_power = s.nextLine();
        Client temp_c = new Client(c_name, c_id, c_pass.toCharArray(),
                Double.parseDouble(c_purchase_power));
        clients.put(temp_c.id, temp_c);
    }

    void listClients() {
        System.out.println("\nClients list:");
        if (clients != null && clients.size() > 0) {
            int sr_no = 1;
            for (Entry<String, Client> entry : clients.entrySet()) {
                System.out.print((sr_no++) + ". " + entry.getValue().toString());
            }
        } else {
            System.out.println("--0 clients found.--");
        }
    }

    @Override
    public void saveToFile() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(new File("./proj2/assets/clients_data.obj")));
            oos.writeObject(clients);
            oos.close();
            // System.out.println("Saved to file");
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
