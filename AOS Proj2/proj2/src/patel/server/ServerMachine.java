package proj2.src.patel.server;

//*********************************************************
//**** Name: Swapnil Patel. Id: 1966690. Course: AOS
//**** Project-2, Date: 04/06/2022
//*********************************************************

import java.util.Scanner;

public class ServerMachine {
    public static void main(String[] args) {
        Server s = new Server();
        Scanner server_scan = new Scanner(System.in);
        s.main_menu(server_scan);
    }
}
