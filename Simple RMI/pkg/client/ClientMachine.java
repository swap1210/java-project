package pkg.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import pkg.registry.ClientRMIInterface;

public class ClientMachine {

    public ClientMachine() throws RemoteException, NotBoundException {
        // localhost assumed replace with server ip in lab
        Registry reg = LocateRegistry.getRegistry("localhost", 922);
        ClientRMIInterface client_stub = (ClientRMIInterface) reg.lookup("my_client");
        System.out.println("" + client_stub.checkCredentials("swap", "password"));
    }
}
