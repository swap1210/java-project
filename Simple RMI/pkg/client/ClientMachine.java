package pkg.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import pkg.registry.ClientRMIInterface;

public class ClientMachine {

    public ClientMachine(Scanner s) throws RemoteException, NotBoundException {
        // localhost assumed replace with server ip in lab
        Registry reg = LocateRegistry.getRegistry("localhost", 922);
        ClientRMIInterface remote_client_stub = (ClientRMIInterface) reg.lookup("my_client");

        // keep on asking for asking username password
        while (true) {
            System.out.println("Username: ");
            String input_username = s.nextLine();
            System.out.println("Password: ");
            String input_password = s.nextLine();
            if (remote_client_stub.checkCredentials(input_username, input_password)) {
                System.out.println("Valid credentials");
            } else {
                System.out.println("Invalid Credentials");
            }
        }
    }
}
