package pkg.registry;

//*********************************************************
//**** Name: Swapnil Patel. Id: 1966690. Course: AOS
//**** Project-3, Date: 04/28/2022
//*********************************************************

import java.rmi.Remote;
import java.rmi.RemoteException;

import pkg.comm.Message;
import pkg.comm.Basic.Topic;

public interface ServiceRMIInterface extends Remote {
    void publish(Message message) throws RemoteException;

    void subscribe(Topic topic, String subscriber_addr) throws RemoteException;

    void unSubscribe(Topic topic, String subscriber_addr) throws RemoteException;
}
