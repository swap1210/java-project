package pkg.server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import pkg.registry.ClientRMIInterface;

public class ServerMachine {
    public ServerMachine(Scanner s) throws RemoteException, AlreadyBoundException {
        Registry reg = LocateRegistry.createRegistry(922);
        ClientRMIInterface client_stub = new Client("sp", "pass");
        reg.bind("my_client", client_stub);
    }
}
