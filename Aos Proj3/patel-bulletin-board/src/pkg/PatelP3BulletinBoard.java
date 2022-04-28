package pkg;

import java.io.IOException;
import java.util.Scanner;

import pkg.broker.PatelP3Service;
import pkg.comm.Basic;
import pkg.comm.MenuClass;
import pkg.pub.PatelP3Publisher;
import pkg.sub.PatelP3Subscriber;

public class PatelP3BulletinBoard {
    public static void main(String[] args) throws IOException {
        Scanner s = new Scanner(System.in);
        MenuClass menuClassObj;
        while (true) {
            try {
                System.out.print(
                        "B for " + Basic.TITLE + "\nP for Publishers\nS for Subscribers\nE to Exit\nChoice: ");
                String choice = s.nextLine();
                if (choice.charAt(0) == 'B' || choice.charAt(0) == 'b') {
                    menuClassObj = new PatelP3Service(s, Basic.TITLE);
                    menuClassObj.menu();
                } else if (choice.charAt(0) == 'P' || choice.charAt(0) == 'p') {
                    System.out.print("Input Publisher Name: ");
                    String pub_name = s.nextLine();
                    menuClassObj = new PatelP3Publisher(s, pub_name);
                    menuClassObj.menu();
                } else if (choice.charAt(0) == 'S' || choice.charAt(0) == 's') {
                    System.out.print("Input Subscriber Name: ");
                    String pub_name = s.nextLine();
                    menuClassObj = new PatelP3Subscriber(s, pub_name);
                    menuClassObj.menu();
                } else if (choice.charAt(0) == 'E' || choice.charAt(0) == 'e') {
                    break;
                }
            } catch (Exception e) {
                System.err.println("Exception in main menu " + e.getMessage());
            }
        }
        s.close();
    }
}