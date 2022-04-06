package proj2.src.patel.registry;

//*********************************************************
//**** Name: Swapnil Patel. Id: 1966690. Course: AOS
//**** Project-2, Date: 04/06/2022
//*********************************************************

import java.io.Serializable;

public class Client implements Serializable {
    String name;
    final String id;
    private char[] pass; // to make this variable more secure than others
    public Wallet wallet;

    public Client(String name, String id, char[] pass, double purchase_power) {
        this.name = name;
        this.id = id;
        this.pass = pass;
        this.wallet = new Wallet();
        this.wallet.balance = purchase_power;
    }

    public boolean checkLogin(String id, String pass) {
        return id.equalsIgnoreCase(this.id) && pass.equalsIgnoreCase(new String(this.pass));
    }

    @Override
    public String toString() {
        String temp = name + " (" + id + ")\n"
                + this.wallet;

        return temp;
    }

    public void editClient(String name, char[] pass, double purchase_power) {
        this.name = name;
        this.pass = pass;
        this.wallet.balance = purchase_power;
    }
}
