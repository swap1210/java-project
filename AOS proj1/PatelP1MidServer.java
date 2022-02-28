
//*********************************************************
//**** Name: Swapnil Patel. Id: 1966690. Course: AOS
//**** Project-1, Date: 02/24/2022
//*********************************************************
//❌✅👋🥳

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
    static Map<String, String> GROUP_LIST = new HashMap<String, String>();

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

    public AuthCred(String username, char[] password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
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

    public static void main(String[] args) throws IOException {

        Scanner scan = new Scanner(System.in);
        System.out.print("Enter New Mid-Server port: ");
        int port = 19675;// scan.nextInt();//
        System.out.println("Port selected " + port);
        ServerSocket ss = new ServerSocket(port);
        Scanner authScan = new Scanner(new File("userList.txt")).useDelimiter("[\\r\\n\\|]+");

        // scan the authentication file to load all creds in a list
        while (authScan.hasNext()) {
            String user = authScan.next();
            char[] pass = authScan.next().toCharArray();
            // debug(new String(pass));
            AUTH_LIST.put(user, new AuthCred(user, pass));
            // debug("File " + AUTH_LIST.get(user).getUsername() + ": password " +
            // AUTH_LIST.get(user).getPass() + " " + (new String(pass)) + " "
            // + AUTH_LIST.get(user).matchPassword(new String(pass)));
        }

        // authScan.close();
        // Add Groups after validating connection
        while (true) {
            try {
                System.out.print(
                        "Enter New Group <ip addr>:<port>\nPRESS 0 to finish loading group\nEnter your choice: ");
                String input = scan.nextLine();
                // debug(input);
                char choice = input.charAt(0);
                if (choice == '0')
                    break;
                else
                    try {
                        // int temp_port = Integer.parseInt(input.split(":")[1]);
                        Socket temp_sock = new Socket(input.split(":")[0], Integer.parseInt(input.split(":")[1]));
                        GROUP_LIST.put(input, input);
                        temp_sock.close();
                    } catch (Exception e) {
                        System.out.println("❌ Failed to add Group(" + input + ")");
                    } finally {
                    }
            } catch (Exception e) {
                System.out.println("❌ Invalid Input");
            }
        }

        while (true) {
            Socket s = null;
            try {
                System.out.println("🕓 Waiting for clients to join");
                s = ss.accept();

                System.out.println("🥳 A new client is connected : " + s);
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                Runnable temp_login = new MyNode(s, dis, dos);
                es.execute(temp_login);

            } catch (Exception e) {
                System.out.println("❌ Error connecting to a client.");
                // e.printStackTrace();
            }
        }

        // es.shutdown();
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

        String option = "", temp_user = "", temp_pass = "", group_ports = "";
        boolean entered = false;
        for (Map.Entry<String, String> set : GROUP_LIST.entrySet()) {
            group_ports += set.getValue() + "\n";
        }

        String menu = "";
        // login loop till not logged in
        while (!loginFlag) {
            menu += "--- Mid Server ---\n";
            if (!entered) {
                menu += "✅ Connected to Main Server\n";
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
            }
        }
        System.out.println("✅ Client login complete");
        // menu = "";

        while (s.isConnected()) {
            try {
                menu = "Logged in as " + temp_user +
                        group_ports;
                menu += "\nCLOSE - Exit\nOption: ";
                dos.writeUTF(menu);
                option = dis.readUTF();
                // if input is group ip and port
                if (GROUP_LIST.get(option) != null) {
                    System.out.println("Client has connected to client this thread can close");
                    break;
                }
            } catch (Exception e) {
                debug("❌ Error while trying to send/receive data from client");
            }
        }

        // while (!s.isClosed()) {
        // try {

        // String menu = "";
        // if (!entered) {
        // menu += "Connected to Main Server\n";
        // entered = true;
        // }

        // // show menu
        // menu += !loginFlag ? "1 - Login" : "Logged in as " + temp_user + "\n" +
        // group_ports;
        // menu += "\nCLOSE - Exit\nOption: ";
        // dos.writeUTF(menu);
        // option = dis.readUTF();

        // // server never breaks
        // if (option.trim().equalsIgnoreCase("???"))
        // break;
        // else if (option.trim().equalsIgnoreCase("1") && !loginFlag) {
        // // Ask user for username
        // dos.writeUTF("username: ");
        // temp_user = dis.readUTF();
        // dos.writeUTF("password: ");
        // temp_pass = dis.readUTF();
        // loginFlag = AUTH_LIST.get(temp_user) != null &&
        // AUTH_LIST.get(temp_user).matchPassword(temp_pass);
        // }

        // if (loginFlag) {
        // String temp_menu = "Login Success\n";
        // System.out.print(temp_menu);
        // temp_menu += group_ports;
        // temp_menu += "Enter Group port: ";
        // dos.writeUTF(temp_menu);
        // temp_pass = dis.readUTF();
        // // if input is group ip and port
        // if (GROUP_LIST.get(temp_pass) != null) {
        // System.out.println("Client has connected to client this thread can close");
        // break;
        // }
        // } else {
        // System.out.println("Login Failed for username(" + temp_user + ") pass(" +
        // temp_pass + ")");
        // }
        // } catch (IOException e) {
        // System.out.println("Closing client thread.");
        // break;
        // // e.printStackTrace();
        // }
        // }

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