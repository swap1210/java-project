
//*********************************************************
//**** Name: Swapnil Patel. Id: 1966690. Course: AOS
//**** Project-1, Date: 02/24/2022
//*********************************************************

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class CommonMidData {

    // authentication details
    static Map<String, AuthCred> AUTH_LIST = new HashMap<String, AuthCred>();
    static Map<String, Socket> GROUP_LIST = new HashMap<String, Socket>();

    static ExecutorService es = Executors.newCachedThreadPool();

    // debugging flag to unable or disable log
    final static boolean DEBUG = true;

    static void debug(String s) {
        if (DEBUG)
            System.out.println("D: " + s);
    }

}

// class to store authentication details securely
class AuthCred {
    private String username;
    private char[] password;
    private String groupName;

    public AuthCred(String username, char[] password, String groupName) {
        this.username = username;
        this.password = password;
        this.groupName = groupName;
    }

    public String getUsername() {
        return username;
    }

    public String getGroupName() {
        return groupName;
    }

    // even with object reference you can't get the password but only match it
    // against this function
    public boolean matchPassword(String toMatch) {
        // System.out.println("comparing " + toMatch + "-" + new String(this.password));
        return (toMatch.equals(new String(this.password)));
    }

    public String getPass() {
        return new String(password);
    }

}

public class PatelP1MidServer extends CommonMidData {

    public static void execute() throws IOException {
        String ms_ip = InetAddress.getLocalHost().toString().split("/")[1];
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter New Mid-Server port: ");
        int port = Integer.parseInt(scan.nextLine());// 81;//
        System.out.println("Port selected " + port);
        ServerSocket ss = new ServerSocket(port);
        System.out.println("Enter Mid-Server IP " + ms_ip + ":" + port);
        Scanner authScan = new Scanner(new File("../assets/userList.txt")).useDelimiter("[\\r\\n\\|]+");

        // scan the authentication file to load all creds in a list
        while (authScan.hasNext()) {
            String user = authScan.next();
            char[] pass = authScan.next().toCharArray();
            String group = authScan.next();
            AUTH_LIST.put(user, new AuthCred(user, pass, group));
        }

        authScan.close();
        // keep on scanning for client
        while (true) {
            Socket s = null;
            try {
                System.out.println("📶  Scanning...");
                s = ss.accept();
                String first_msg = new DataInputStream(s.getInputStream()).readUTF();
                if (first_msg.contains("Silver:") || first_msg.contains("Gold:") || first_msg.contains("Platinum:")) {
                    String temp_groupName = first_msg.split(":")[0];
                    GROUP_LIST.put(temp_groupName, s);
                    System.out.println(
                            "➕ " + temp_groupName + " Group(" + GROUP_LIST.get(temp_groupName).getInetAddress() + ":"
                                    + GROUP_LIST.get(temp_groupName).getPort() + ") Added");
                } else {
                    System.out.println("🔗 Sender(" + s.getInetAddress() + ":" + s.getPort() + ") is connected");
                    Runnable temp_login = new MyNode(s);
                    es.execute(temp_login);
                }

            } catch (Exception e) {
                System.out.println("❌ Error connecting to a client.");
            }
        }
    }

}

class MyNode extends CommonMidData implements Runnable {

    final Socket s;
    final DataInputStream dis;
    final DataOutputStream dos;
    boolean loginFlag = false;

    public MyNode(Socket s) throws IOException {
        this.s = s;
        this.dis = new DataInputStream(s.getInputStream());
        this.dos = new DataOutputStream(s.getOutputStream());
    }

    public void run() {

        String option = "", temp_user = "", temp_pass = "";
        boolean entered = false;
        String menu = "";
        // login loop till not logged in
        while (!loginFlag && !s.isClosed()) {
            menu += "--- Mid Server ---\n";
            if (!entered) {
                menu += "🔗 Connected to Main Server\n";
                entered = true;
            }
            menu += "Press 1 to Login\nPress CLOSE to Exit\n";
            try {
                dos.writeUTF(menu);
                option = dis.readUTF();
                if (option.trim().equalsIgnoreCase("1") && !loginFlag) {
                    // Ask user for username
                    dos.writeUTF("👤 Send username ");
                    temp_user = dis.readUTF();
                    dos.writeUTF("🔑 Send password ");
                    temp_pass = dis.readUTF();
                    loginFlag = AUTH_LIST.get(temp_user) != null &&
                            AUTH_LIST.get(temp_user).matchPassword(temp_pass);
                    if (loginFlag) {
                        break;
                    } else {
                        menu = "❌ Invalid credentials\n";
                    }
                }
            } catch (Exception e) {
                debug("❌ Error while trying to send/receive data from client");
                return;
            }
        }
        System.out.println("✅ Sender(" + s.getPort() + ") login complete");

        // find group in the list
        Socket selected_Group = null;
        DataInputStream g_dis = null;
        DataOutputStream g_dos = null;
        try {
            selected_Group = GROUP_LIST.get(AUTH_LIST.get(temp_user).getGroupName());
            g_dis = new DataInputStream(selected_Group.getInputStream());
            g_dos = new DataOutputStream(selected_Group.getOutputStream());
        } catch (Exception e) {
            debug("❌ Unavailable Group Server");
            try {
                dos.writeUTF("❌ " + AUTH_LIST.get(temp_user).getGroupName() + " Group Server was unavailable\n");
                s.close();
            } catch (IOException e1) {
            }
            return;
        }

        // check if the group exist in the mid server or not
        if (s != null && selected_Group != null)
            debug(!s.isClosed() + " - " + !selected_Group.isClosed());

        try {
            // send welcome message to connected Client
            g_dos.writeUTF("Sender(" + s.getLocalAddress() + ":" + s.getPort() + ") Requested Quote");
            // communication establishing between GROUP and CLIENT
            while (!s.isClosed() && !selected_Group.isClosed()) {
                String group_input = g_dis.readUTF();
                dos.writeUTF(group_input);
                String client_input = dis.readUTF();
                if (client_input.contains("CLOSE"))
                    break;
                g_dos.writeUTF("Sender(" + s.getLocalAddress() + ":" + s.getPort() + ") :" + client_input);

            }
        } catch (Exception e) {
            System.out.println("❌ Error between client and group");
        }

        try {
            // closing resources
            this.dis.close();
            this.dos.close();
            this.s.close();
            System.out.println("Closed this client");
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

}