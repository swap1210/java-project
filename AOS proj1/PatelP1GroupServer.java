
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class PatelP1GroupServer extends CommonData {

    public static void main(String[] args) throws IOException {
        try {
            Scanner scn = new Scanner(System.in);
            System.out.print("Enter New Group port: ");
            int port = scn.nextInt();
            ServerSocket ss = new ServerSocket(19668);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
