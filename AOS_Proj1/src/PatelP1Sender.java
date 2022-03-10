
//*********************************************************
//**** Name: Swapnil Patel. Id: 1966690. Course: AOS
//**** Project-1, Date: 02/24/2022
//*********************************************************

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class PatelP1Sender {

    public static void execute() {

        // mid server socket
        Socket ms_soc = null;
        DataInputStream ms_soc_dis = null;
        DataOutputStream ms_soc_dos = null;
        try {
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
            ms_soc_dos.writeUTF("Sender");

            while (!ms_soc.isClosed()) {
                String serverInput = ms_soc_dis.readUTF();
                System.out.print(serverInput);
                // if Mid-server couldn't find user's group quit
                if (serverInput.contains(" Group Server was unavailable"))
                    break;
                System.out.print("To Mid-Server: ");
                String tosend = scn.nextLine();

                // If client sends CLOSE,close this connection
                // and then break from the while loop
                if (tosend.trim().equalsIgnoreCase("CLOSE")) {
                    System.out.println("Closing this connection : " + ms_soc);
                    // System.out.println("Connection closed");
                    break;
                }

                ms_soc_dos.writeUTF(tosend);
            }

            // closing all resources
            try {
                scn.close();
                ms_soc_dis.close();
                ms_soc_dos.close();
                ms_soc.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Connection Error");
        }
    }

}
