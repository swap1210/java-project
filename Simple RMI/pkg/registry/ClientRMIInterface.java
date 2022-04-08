package pkg.registry;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientRMIInterface extends Remote {
    boolean checkCredentials(String username, String password) throws RemoteException;
}
