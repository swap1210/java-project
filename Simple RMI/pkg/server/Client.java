package pkg.server;

import java.io.Serializable;
import java.rmi.RemoteException;

import pkg.registry.ClientRMIInterface;

public class Client implements Serializable, ClientRMIInterface {
    private String username;
    private String password;

    public Client(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean checkCredentials(String username, String password) throws RemoteException {
        return this.username.equalsIgnoreCase(username) && this.password.equalsIgnoreCase(password);

    }
}
