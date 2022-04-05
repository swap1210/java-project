package proj2.src.patel.server;

import java.util.Scanner;

public class ServerMachine {
    public static void main(String[] args) {
        Server s = new Server();
        Scanner server_scan = new Scanner(System.in);
        s.main_menu(server_scan);
    }
}
