package pkg.comm;

//*********************************************************
//**** Name: Swapnil Patel. Id: 1966690. Course: AOS
//**** Project-3, Date: 04/28/2022
//*********************************************************

import java.net.SocketException;
import java.util.Scanner;

public abstract class MenuClass {
    public Scanner scan;
    public String name;

    public MenuClass(Scanner scan, String name) {
        this.scan = scan;
        this.name = name;
    }

    public abstract void menu() throws SocketException;
}
