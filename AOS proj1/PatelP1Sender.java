
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class PatelP1Sender {

    static Socket group_socket = null;

    public static void main(String[] args) throws IOException {
        try {
            Scanner scn = new Scanner(System.in);

            // establish the connection with server port 19675
            System.out.print("Enter Mid-server <ip addr>:<port> ");
            String temp = scn.nextLine();// "localhost";//
            String serverip = temp.split(":")[0]; // scn.nextLine();// "localhost";
            // System.out.print("Enter Mid-server port: ");
            String serverport = temp.split(":")[1]; // scn.nextLine();// "localhost";
            // System.out.println();
            Socket s = new Socket((serverip.equals("") ? "localhost" : serverip), Integer.parseInt(serverport));
            // obtaining input and out streams from Mid server
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            // the following loop performs the exchange of
            // information between client and client handler
            while (true) {
                String tosend = "";
                System.out.print(dis.readUTF());
                tosend = scn.nextLine();

                // If client sends CLOSE,close this connection
                // and then break from the while loop
                if (tosend.equalsIgnoreCase("CLOSE")) {
                    System.out.println("Closing this connection : " + s);
                    s.close();
                    // System.out.println("Connection closed");
                    break;
                } else {
                    // this input could be potential group socket so wait
                    try {
                        group_socket = new Socket(tosend.split(":")[0], Integer.parseInt(tosend.split(":")[1]));
                        // on connection with direct Group socket break out of Middleserver loop
                        System.out.println("Closing mid server socket as group is connected");
                        s.close();
                        break;
                    } catch (Exception e) {

                    }
                }

                dos.writeUTF(tosend);
            }

            // obtaining input and out streams from Mid server
            dis = new DataInputStream(group_socket.getInputStream());
            dos = new DataOutputStream(group_socket.getOutputStream());

            // final group connection loop
            while (group_socket != null) {

                System.out.println(dis.readUTF());
                dos.writeUTF(scn.nextLine());
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
