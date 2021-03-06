package pkg.comm;

//*********************************************************
//**** Name: Swapnil Patel. Id: 1966690. Course: AOS
//**** Project-3, Date: 04/28/2022
//*********************************************************

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class Basic {

    public enum Topic {
        TOP_NEWS, SPORTS, HEALTH, LIFESTYLE, EDUCATION, OTHER
    }

    public static String TITLE = "Bulletin Board Service";

    public static String[] displayInterfaceInformation(int port) {
        List<String> ips = new ArrayList<String>();
        Enumeration<NetworkInterface> nets;
        try {
            nets = NetworkInterface.getNetworkInterfaces();
            String stars = "+-----------------------------+";
            System.out.println(stars);
            System.out.printf("|%23s %5s|\n", "Addresses available:", "");
            System.out.println(stars);
            for (NetworkInterface netint : Collections.list(nets)) {
                Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                String ip_list = "";
                for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                    String temp_ip = String.format("%s", inetAddress);
                    if (temp_ip.contains(":") || temp_ip.contains("127.0.0.1"))
                        continue;
                    ips.add(temp_ip.substring(1) + ":" + port);
                    ip_list += String.format("%23s |\n", temp_ip.substring(1) + ":" + port);
                }
                if (!ip_list.equalsIgnoreCase("")) {
                    System.out.printf("|%3s: ", netint.getName());
                    System.out.print(ip_list);
                }
            }
            System.out.println(stars);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ips.toArray(new String[0]);
    }
}
