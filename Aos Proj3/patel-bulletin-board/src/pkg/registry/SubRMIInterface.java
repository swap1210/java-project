package pkg.registry;

//*********************************************************
//**** Name: Swapnil Patel. Id: 1966690. Course: AOS
//**** Project-3, Date: 04/28/2022
//*********************************************************

import java.rmi.Remote;
import java.rmi.RemoteException;

import pkg.comm.Message;

public interface SubRMIInterface extends Remote {
    void onPublish(Message message) throws RemoteException;

}
