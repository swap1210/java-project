package pkg;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import pkg.broker.PatelP3Service;
import pkg.comm.AuthCred;
import pkg.comm.Basic;
import pkg.comm.MenuClass;
import pkg.comm.AuthCred.NodeType;
import pkg.pub.PatelP3Publisher;
import pkg.sub.PatelP3Subscriber;

public class PatelP3BulletinBoard {
    private static Map<String, AuthCred> AUTH_MAP = new HashMap<String, AuthCred>();

    public static void main(String[] args) throws IOException {
        Scanner s = new Scanner(System.in);
        MenuClass menuClassObj;
        loadAuthList();
        while (true) {
            try {
                System.out.print("Login username:pass or Exit: ");
                String cred = s.nextLine();
                if (cred.toLowerCase().contains("xit"))
                    break;
                AuthCred tempCred = AUTH_MAP.get(cred.split(":")[0]);
                if (tempCred == null || !tempCred.matchPassword(cred.split(":")[1])) {
                    System.err.println("Invalid Credentials");
                    continue;
                }
                switch (tempCred.getNodeType()) {
                    case BBS:
                        menuClassObj = new PatelP3Service(s, Basic.TITLE);
                        menuClassObj.menu();
                        break;
                    case PUB:
                        System.out.print("Input Publisher Name: ");
                        String pub_name = s.nextLine();
                        menuClassObj = new PatelP3Publisher(s, pub_name);
                        menuClassObj.menu();
                        break;
                    case SUB:
                        menuClassObj = new PatelP3Subscriber(s, "Subscriber");
                        menuClassObj.menu();
                        break;
                }
            } catch (Exception e) {
                System.err.println("Exception in main menu " + e.getMessage());
            }
        }
        s.close();

    }

    static void loadAuthList() {
        AUTH_MAP.put("bbs", new AuthCred(NodeType.BBS, "bbs", "pass".toCharArray()));
        AUTH_MAP.put("p1_pub", new AuthCred(NodeType.PUB, "p1_pub", "pass".toCharArray()));
        AUTH_MAP.put("p2_pub", new AuthCred(NodeType.PUB, "p2_pub", "pass".toCharArray()));
        AUTH_MAP.put("p3_pub", new AuthCred(NodeType.PUB, "p3_pub", "pass".toCharArray()));
        AUTH_MAP.put("p4_pub", new AuthCred(NodeType.PUB, "p4_pub", "pass".toCharArray()));
        AUTH_MAP.put("p5_pub", new AuthCred(NodeType.PUB, "p5_pub", "pass".toCharArray()));
        AUTH_MAP.put("s1_sub", new AuthCred(NodeType.SUB, "s1_sub", "pass".toCharArray()));
        AUTH_MAP.put("s2_sub", new AuthCred(NodeType.SUB, "s2_sub", "pass".toCharArray()));
    }
}