package pkg.registry;

//*********************************************************
//**** Name: Swapnil Patel. Id: 1966690. Course: AOS
//**** Project-3, Date: 04/28/2022
//*********************************************************

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import pkg.comm.Message;
import pkg.comm.Basic.Topic;

public interface ServiceRMIInterface extends Remote {
    void publish(Message message) throws RemoteException;

    void subscribe(Topic topic, String subscriber_addr) throws RemoteException;

    void unSubscribe(Topic topic, String subscriber_addr) throws RemoteException;

    List<Message> getAllPublications(Topic topic) throws RemoteException;
}
