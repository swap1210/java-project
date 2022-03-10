
//*********************************************************
//**** Name: Swapnil Patel. Id: 1966690. Course: AOS
//**** Project-1, Date: 02/24/2022
//*********************************************************

import java.io.IOException;
import java.util.Scanner;

public class MainExecutor {

    public static void main(String[] s) throws IOException {
        System.out.print("Choose the type of Program:\n1. Mid-Server\n2. Group Server\n3. Sender\nYour choice:");
        Scanner sc = new Scanner(System.in);
        int choice = 0;
        while (true) {
            try {
                choice = Integer.parseInt(sc.nextLine());
                break;
            } catch (NumberFormatException ne) {
                System.out.println("Invalid Input");
                continue;
            }
        }

        switch (choice) {
            case 1:
                PatelP1MidServer.execute();
                break;
            case 2:
                PatelP1GroupServer.execute();
                break;
            case 3:
                PatelP1Sender.execute();
                break;
        }
    }
}