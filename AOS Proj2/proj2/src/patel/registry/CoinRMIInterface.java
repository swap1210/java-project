package proj2.src.patel.registry;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CoinRMIInterface extends Remote {
    public String printDetails() throws RemoteException;
}
