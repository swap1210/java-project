package pkg.server;

import java.io.Serializable;
import java.rmi.RemoteException;

import pkg.registry.ClientRMIInterface;

public class Client implements Serializable, ClientRMIInterface {
    private String username;
    private String password;
    private double wallet;
 
    public Client(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void buy(int amt){

    }

    @Override
    public void sell(int qty){
        
    }

    @Override
    public boolean checkCredentials(String username, String password) throws RemoteException {
        System.out.println("called checkCredentials");
        return this.username.equalsIgnoreCase(username) && this.password.equalsIgnoreCase(password);

    }
}
