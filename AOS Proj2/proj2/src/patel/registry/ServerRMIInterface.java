package proj2.src.patel.registry;

//*********************************************************
//**** Name: Swapnil Patel. Id: 1966690. Course: AOS
//**** Project-2, Date: 04/06/2022
//*********************************************************

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerRMIInterface extends Remote {
    String performPurchase(String user_id, boolean buying, String currency_code, double amt) throws RemoteException;

    public Client getClient(String client_id, String client_pass) throws RemoteException;

}
