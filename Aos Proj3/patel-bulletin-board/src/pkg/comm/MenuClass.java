package pkg.comm;

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
