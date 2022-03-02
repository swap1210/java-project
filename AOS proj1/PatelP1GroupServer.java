
//*********************************************************
//**** Name: Swapnil Patel. Id: 1966690. Course: AOS
//**** Project-1, Date: 02/24/2022
//*********************************************************

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    static String shopName[] = { "gold", "silver", "platinum" };
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

    // Initializing thread pool
    static ExecutorService es = Executors.newCachedThreadPool();

    static int port;

    static void debug(String s) {
        if (DEBUG)
            System.out.println("D: " + s);
    }

    String printMenu() {
        if (GroupTypeId < 0 || GroupTypeId > 2) {
            return "Invalid Group choice";
        }
        String temp = "Sr No.\tID\t\tItem Name\tQty\n";
        for (int i = 0; i < items[GroupTypeId].length; i++) {
            temp += (i + 1) + ".\t" + items[GroupTypeId][i].itemId + "\t" + items[GroupTypeId][i].itemName + "\t"
                    + items[GroupTypeId][i].qty + "\t"
                    + "\n";
        }

        return temp;
    }
}

public class PatelP1GroupServer extends CommonGroupData {
    public static void main(String[] args) throws IOException {
        Scanner scn = new Scanner(System.in);
        System.out.print("Enter New Group port: ");
        port = scn.nextInt();
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

        ServerSocket ss = new ServerSocket(port);
        while (true) {
            Socket s = null;
            try {
                System.out.println("📶 " + shopName[GroupTypeId].substring(0, 1).toUpperCase()
                        + shopName[GroupTypeId].substring(1) + " Group(" + port + ") Scanning...");
                s = ss.accept();

                System.out.println("🔗 Mid Server(" + s.getInetAddress() + ":" + s.getPort() + ") is connected : " + s);
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                Runnable temp_login = new GroupNode(s, dis, dos);
                es.execute(temp_login);
            } catch (Exception e) {
            }
        }
    }
}

class GroupNode extends CommonGroupData implements Runnable {
    final Socket s;
    final DataInputStream dis;
    final DataOutputStream dos;

    public GroupNode(Socket s, DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.s = s;
    }

    public void run() {
        boolean entered = false;

        try {
            while (true) {
                dos.writeUTF(shopName[GroupTypeId]);
                String menu = "";
                if (!entered) {
                    menu += "Welcome to Group(" + port + ") Server\n";
                    entered = true;
                }
                menu += printMenu();
                dos.writeUTF(menu);
                System.out.println(dis.readUTF());
            }
        } catch (Exception e) {
            System.out.println("❌ Connection closed by client.");
        }
    }
}