
//*********************************************************
//**** Name: Swapnil Patel. Id: 1966690. Course: AOS
//**** Project-1, Date: 02/24/2022
//*********************************************************

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
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
    static Map<String, GroupDetails> GROUP_LIST = new HashMap<String, GroupDetails>();

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

class GroupDetails {
    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;

    public GroupDetails(Socket socket, DataInputStream dis, DataOutputStream dos) {
        this.socket = socket;
        this.dis = dis;
        this.dos = dos;
    }

    @Override
    public String toString() {
        return "GroupDetails [dis=" + dis + ", dos=" + dos + ", socket=" + socket + "]";
    }

}

public class PatelP1MidServer extends CommonMidData {

    public static void main(String[] args) throws IOException {

        Scanner scan = new Scanner(System.in);
        System.out.print("Enter New Mid-Server port: ");
        int port = Integer.parseInt(scan.nextLine());// 19675;//
        System.out.println("Port selected " + port);
        ServerSocket ss = new ServerSocket(port);
        System.out.println("Mid Server IP " + ss.getLocalSocketAddress());
        Scanner authScan = new Scanner(new File("userList.txt")).useDelimiter("[\\r\\n\\|]+");

        // scan the authentication file to load all creds in a list
        while (authScan.hasNext()) {
            String user = authScan.next();
            char[] pass = authScan.next().toCharArray();
            String group = authScan.next();
            AUTH_LIST.put(user, new AuthCred(user, pass, group));
        }

        // Add Groups after validating connection
        while (true) {
            try {
                System.out.print(
                        "Enter Group host:port \nPRESS 0 to finish loading group\nEnter your choice: ");
                String input = scan.nextLine();
                // debug(input);
                char choice = input.charAt(0);
                if (choice == '0')
                    break;
                else {
                    try {
                        // int temp_port = Integer.parseInt(input.split(":")[1]);
                        Socket temp_sock = new Socket(input.split(":")[0], Integer.parseInt(input.split(":")[1]));
                        DataInputStream temp_dis = new DataInputStream(temp_sock.getInputStream());
                        DataOutputStream temp_dos = new DataOutputStream(temp_sock.getOutputStream());
                        String temp_groupName = temp_dis.readUTF();
                        GROUP_LIST.put(temp_groupName, new GroupDetails(temp_sock, temp_dis, temp_dos));
                        System.out.println("➕ Group(" + GROUP_LIST.get(temp_groupName).socket.getPort() + ") Added");
                    } catch (Exception e) {
                        System.out.println("❌ Failed to add Group(" + input + ")");
                    }
                }
            } catch (Exception e) {
                System.out.println("❌ Invalid Input");
            }
        }

        while (true) {
            Socket s = null;
            try {
                System.out.println("📶  Scanning...");
                s = ss.accept();
                System.out.println("🔗 client(" + s.getLocalPort() + ") is connected : " + s);
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                Runnable temp_login = new MyNode(s, dis, dos);
                es.execute(temp_login);

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

    public MyNode(Socket s, DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.s = s;
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
            menu += "Press 1 to Login\nPress CLOSE to Exit\nOption: ";
            try {
                dos.writeUTF(menu);
                option = dis.readUTF();
                if (option.trim().equalsIgnoreCase("1") && !loginFlag) {
                    // Ask user for username
                    dos.writeUTF("👤 username: ");
                    temp_user = dis.readUTF();
                    dos.writeUTF("🔑 password: ");
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
                break;
            }
        }
        System.out.println("✅ Client(" + s.getPort() + ") login complete");

        // communication establishing between GROUP and CLIENT
        while (!s.isClosed() && !GROUP_LIST.get(AUTH_LIST.get(temp_user).getGroupName()).socket.isClosed()) {
            try {
                dos.writeUTF(GROUP_LIST.get(AUTH_LIST.get(temp_user).getGroupName()).dis.readUTF());
                String client_input = dis.readUTF();
                if (client_input.contains("CLOSE"))
                    break;
                GROUP_LIST.get(AUTH_LIST.get(temp_user).getGroupName()).dos
                        .writeUTF("Client (" + s.getPort() + ") :" + client_input);
            } catch (Exception e) {
                System.out.println("❌ Error between client and group");
            }
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