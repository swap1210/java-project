package pkg.server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;
import java.util.Scanner;

import pkg.registry.ClientRMIInterface;

public class ServerMachine {
    public ServerMachine(Scanner s) throws RemoteException, AlreadyBoundException {
        Registry reg = LocateRegistry.createRegistry(922);
        List<Client> clients = new LinkedList<Client>();
        clients.add(new Client("sp", "pass"));
        reg.bind("my_client", client_stub);
    }
}
