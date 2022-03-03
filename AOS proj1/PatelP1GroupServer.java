
//*********************************************************
//**** Name: Swapnil Patel. Id: 1966690. Course: AOS
//**** Project-1, Date: 02/24/2022
//*********************************************************

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

class Item {
    String itemId;
    String itemName;
    int qty;

    public Item(String itemId, String itemName, int qty) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.qty = qty;
    }

}

class CommonGroupData {

    // debugging flag to unable or disable debug log
    final static boolean DEBUG = false;

    static int GroupTypeId = -1;
    static String shopName[] = { "Gold", "Silver", "Platinum" };
    static Item items[][] = { {
            new Item("16-931-5891", "Eagle", 15),
            new Item("35-563-0816", "Subaru", 13),
            new Item("36-866-1896", "Honda", 7),
            new Item("10-023-8090", "Ford", 10),
            new Item("84-540-7641", "Toyota", 3),
            new Item("05-430-4133", "Mazda", 2),
            new Item("66-513-2092", "Porsche", 1),
            new Item("23-579-4308", "Nissan", 20),
            new Item("55-998-2001", "Nissan", 10),
            new Item("45-539-4417", "BMW", 4),
    },
            {
                    new Item("49-495-0527", "Benazepril", 19),
                    new Item("48-915-7846", "Bacti-Free tm", 8),
                    new Item("18-441-6379", "SUPER AQUA", 17),
                    new Item("53-231-5599", "AcneFree", 6),
                    new Item("27-094-2351", "Triamcinolone", 12),
                    new Item("67-063-9624", "Dexamethasone", 17),
            },
            {
                    new Item("74-330-4385", "Misoprostol", 5),
                    new Item("67-072-9255", "Escitalopram", 15),
                    new Item("22-865-2617", "Guaifenesin", 18),
                    new Item("27-568-7449", "Dextromethorphan", 1),
            } };

    static void debug(String s) {
        if (DEBUG)
            System.out.println("D: " + s);
    }

    static String printMenu() {
        if (GroupTypeId < 0 || GroupTypeId > 2) {
            return "Invalid Group choice";
        }
        String temp = "Sr No.\tID\tItem Name\tQty\n";
        for (int i = 0; i < items[GroupTypeId].length; i++) {
            // skip 0 qty
            if (items[GroupTypeId][i].qty <= 0)
                continue;
            temp += (i + 1) + ".\t" + items[GroupTypeId][i].itemId + "\t" + items[GroupTypeId][i].itemName + "\t\t"
                    + items[GroupTypeId][i].qty
                    + "\n";
        }

        return temp;
    }
}

public class PatelP1GroupServer extends CommonGroupData {

    public static void main(String[] args) throws IOException {
        Socket ms_soc = null;
        DataInputStream ms_soc_dis = null;
        DataOutputStream ms_soc_dos = null;
        Scanner scn = new Scanner(System.in);

        // establish the connection with Mid Server port
        String temp = "";
        while (!temp.contains(":")) {
            System.out.print("Enter Mid-Server IP: ");
            temp = scn.nextLine();// "localhost:81";//
            if (!temp.contains(":")) {
                System.out.println("❌ Invalid Input");
                continue;
            }
        }

        String serverip = temp.split(":")[0]; // scn.nextLine();// "localhost";
        String serverport = temp.split(":")[1]; // scn.nextLine();// "localhost";
        ms_soc = new Socket(serverip, Integer.parseInt(serverport));
        // obtaining input and out streams from Mid server
        ms_soc_dis = new DataInputStream(ms_soc.getInputStream());
        ms_soc_dos = new DataOutputStream(ms_soc.getOutputStream());

        int option = -1;
        while (option < 0 || option > 2) {
            System.out.println("Select Group preset:");
            System.out.println("0: Gold");
            System.out.println("1: Silver");
            System.out.println("2: Platinum");
            System.out.println("Option: ");
            option = scn.nextInt();
            if (option < 0 || option > 2) {
                System.out.println("❌ Invalid Input!");
                continue;
            } else {
                GroupTypeId = option;
            }
        }

        try {
            System.out.println("📶 " + shopName[GroupTypeId].substring(0, 1).toUpperCase()
                    + shopName[GroupTypeId].substring(1) + " Group Scanning...");

            System.out.println("🔗 Mid Server(" + ms_soc.getInetAddress() + ":" + ms_soc.getPort() + ") is connected");
            DataInputStream dis = new DataInputStream(ms_soc.getInputStream());
            DataOutputStream dos = new DataOutputStream(ms_soc.getOutputStream());

            while (!ms_soc.isClosed()) {
                String menu = "";
                menu += shopName[GroupTypeId] + ": Welcome to Group Server\n";
                menu += printMenu();
                // System.out.println("Sending " + menu);
                dos.writeUTF(menu);
                String clientInput = dis.readUTF();
                System.out.println(clientInput);
                try {
                    int choice = Integer.parseInt(clientInput.split(":")[2]) - 1;
                    synchronized (items) {
                        if (choice >= 0)
                            items[GroupTypeId][choice].qty--;
                        System.out.println(clientInput.substring(0, clientInput.lastIndexOf(':')) + "Purchased "
                                + items[GroupTypeId][choice].itemName);
                    }
                } catch (Exception e) {
                    System.out.println("Weird Message" + e.getMessage());
                }
            }

            ms_soc.close();
            ms_soc_dis.close();
            ms_soc_dos.close();
        } catch (Exception e) {
            System.out.println("Connection closed abruptly");
        }
    }
}