
import java.io.*;
import java.net.*;

// Server class
public class PatelP1GroupServer {
    public static void main(String[] args) throws IOException {
        // server is listening on port 5056
        ServerSocket ss = new ServerSocket(5056);

        // running infinite loop for getting
        // client request
        System.out.println("Connect me using " + ss.getInetAddress());
        int counter = 0;
        while (true) {
            Socket s = null;

            try {
                // socket object to receive incoming client requests
                s = ss.accept();

                System.out.println("A new client is connected : " + s);

                // obtaining input and out streams
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                System.out.println("Assigning new thread for this client");

                // create a new thread object
                Thread t = new PatelP1MidServer(counter++, s, dis, dos);

                // Invoking the start() method
                t.start();

            } catch (Exception e) {
                // s.close();
                e.printStackTrace();
            } finally {
                // ss.close();
            }
        }
    }
}
