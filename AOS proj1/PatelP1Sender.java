
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class PatelP1Sender {
    public static void main(String[] args) throws IOException {
        try {
            Scanner scn = new Scanner(System.in);

            // establish the connection with server port 19667
            System.out.print("Enter Mid-server ip: ");
            String serverip = "localhost"; // scn.nextLine();// "localhost";
            System.out.print("Enter Mid-server port: ");
            String serverport = "19667"; // scn.nextLine();// "localhost";
            System.out.println();
            Socket s = new Socket((serverip.equals("") ? "localhost" : serverip), Integer.parseInt(serverport));
            // obtaining input and out streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            // the following loop performs the exchange of
            // information between client and client handler
            while (true) {
                // while (dis.available() > 0)
                System.out.print(dis.readUTF());
                String tosend = scn.nextLine();
                dos.writeUTF(tosend);

                // If client sends exit,close this connection
                // and then break from the while loop
                if (tosend.equalsIgnoreCase("CLOSE")) {
                    System.out.println("Closing this connection : " + s);
                    s.close();
                    System.out.println("Connection closed");
                    break;
                }
            }

            // closing resources
            try {
                scn.close();
                dis.close();
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
