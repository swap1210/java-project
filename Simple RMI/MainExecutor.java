import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

import pkg.client.ClientMachine;
import pkg.server.ServerMachine;

public class MainExecutor {
    public static void main(String[] args) throws RemoteException, AlreadyBoundException, NotBoundException {
        Scanner s = new Scanner(System.in);
        System.out.print("S/C for Server or client ");
        String choice = s.nextLine();
        if (choice.equalsIgnoreCase("S")) {
            System.out.println("Server");
            ServerMachine ss = new ServerMachine();
            System.out.print("Enter to exit server");
            s.nextLine();
        } else {
            System.out.println("Client");
            ClientMachine cc = new ClientMachine();
        }
        s.close();
    }
}
