
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class CommonGroupData {

    // debugging flag to unable or disable log
    final static boolean DEBUG = true;
    // Initializing thread pool
    static ExecutorService es = Executors.newCachedThreadPool();

    static int port;

    static void debug(String s) {
        if (DEBUG)
            System.out.println("D: " + s);
    }

}

public class PatelP1GroupServer extends CommonGroupData {

    public static void main(String[] args) throws IOException {
        Scanner scn = new Scanner(System.in);
        System.out.print("Enter New Group port: ");
        port = scn.nextInt();// 19668;//
        ServerSocket ss = new ServerSocket(port);
        while (true) {
            Socket s = null;
            try {

                System.out.println("🕓 Group " + port + " waiting for clients to join");
                s = ss.accept();

                System.out.println("🥳 A new client is connected : " + s);
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                Runnable temp_login = new GroupNode(s, dis, dos);
                es.execute(temp_login);
            } catch (Exception e) {
                // e.printStackTrace();
            } finally {
                // scn.close();
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

        while (true) {

            try {
                String menu = "";
                if (!entered) {
                    menu += "Welcome to Group(" + port + ") Server\n";
                    entered = true;
                }
                dos.writeUTF(menu);
                System.out.println(dis.readUTF());

            } catch (Exception e) {
                // TODO: handle exception
                System.out.println("❌ Connection closed by client.");
            }
        }
    }

}