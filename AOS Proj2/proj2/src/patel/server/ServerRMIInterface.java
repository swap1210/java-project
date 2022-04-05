package proj2.src.patel.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import proj2.src.patel.registry.Client;
import proj2.src.patel.registry.Coin;

public interface ServerRMIInterface extends Remote {
    String performPurchase(String user_id, boolean buying, String currency_code, double amt) throws RemoteException;

    public Map<String, Client> getClientList() throws RemoteException;

    public Map<String, Coin> getCoinList() throws RemoteException;
}
