
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

class CommonData {

    // authentication details
    static Map<String, AuthCred> AUTH_LIST = new HashMap<String, AuthCred>();
    static Map<Integer, Socket> GROUP_LIST = new HashMap<Integer, Socket>();

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

public class PatelP1MidServer extends CommonData {

    static ExecutorService es = Executors.newCachedThreadPool();

    public static void main(String[] args) throws IOException {

        ServerSocket ss = new ServerSocket(19667);
        Scanner scan = new Scanner(System.in);
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

        authScan.close();
        // Add Groups based on port numbers
        boolean exit_flag = false;
        while (!exit_flag) {
            System.out.println("Enter group ip:port or 0 to complete: ");
            String input = scan.next();
            char choice = input.charAt(0);
            switch (choice) {
                case '0':
                    exit_flag = true;
                    break;
                default:
                    try {
                        int temp_port = Integer.parseInt(input.split(":")[1]);
                        Socket temp_sock = new Socket(input.split(":")[0], temp_port);
                        GROUP_LIST.put(temp_port, temp_sock);
                    } catch (Exception e) {
                        System.out.println("Failed to add group");
                    }
                    break;
            }
        }

        int x = 2;
        while (1 == x) {
            Socket s = null;
            try {
                System.out.println("Waiting for clients to join 🕓");
                s = ss.accept();

                System.out.println("A new client is connected : " + s);
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                Runnable temp_login = new MyNode(s, dis, dos);
                es.execute(temp_login);

            } catch (Exception e) {
                System.out.println("Error connecting to 1 client ");
                // e.printStackTrace();
            }
        }

        // es.shutdown();
    }

}

class MyNode extends CommonData implements Runnable {

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
        while (true) {
            try {

                String menu = "";
                if (!entered) {
                    menu += "Connected to Main Server\n";
                    entered = true;
                }

                // show menu
                menu += !loginFlag ? "1 - Login" : "Logged in as " + temp_user;
                menu += "\nCLOSE - Exit\nOption: ";
                dos.writeUTF(menu);
                option = dis.readUTF();

                // server never breaks
                if (option.trim().equalsIgnoreCase("???"))
                    break;
                else if (option.trim().equalsIgnoreCase("1") && !loginFlag) {
                    // Ask user for username
                    dos.writeUTF("username: ");
                    temp_user = dis.readUTF();
                    dos.writeUTF("password: ");
                    temp_pass = dis.readUTF();
                    loginFlag = AUTH_LIST.get(temp_user) != null && AUTH_LIST.get(temp_user).matchPassword(temp_pass);
                }

                if (loginFlag) {
                    String temp_menu = "Login Success\n";
                    System.out.print(temp_menu);
                    for (Map.Entry<Integer, Socket> set : GROUP_LIST.entrySet()) {
                        temp_menu += set.getKey() + "\n";
                    }
                    temp_menu += "Enter Group port: ";
                    dos.writeUTF(temp_menu);
                    temp_pass = dis.readUTF();

                } else {
                    System.out.println("Login Failed for username(" + temp_user + ") pass(" + temp_pass + ")");
                }
            } catch (IOException e) {
                System.out.println("Closing client thread.");
                break;
                // e.printStackTrace();
            }
        }

        try {
            // closing resources
            this.dis.close();
            this.dos.close();

        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

}