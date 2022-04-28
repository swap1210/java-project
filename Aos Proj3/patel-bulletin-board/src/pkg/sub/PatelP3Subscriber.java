package pkg.sub;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import pkg.comm.Basic;
import pkg.comm.MenuClass;
import pkg.comm.Message;
import pkg.comm.Basic.Topic;
import pkg.registry.ServiceRMIInterface;
import pkg.registry.SubRMIInterface;

public class PatelP3Subscriber extends MenuClass implements SubRMIInterface {
    int port;
    String myip;
    private ServiceRMIInterface service_stub;
    List<Topic> subscription_list;

    public PatelP3Subscriber(Scanner s, String client_name) {
        super(s, client_name);
        subscription_list = new LinkedList<Topic>();
        System.out.print("Input subscriber port: ");
        port = Integer.parseInt(s.nextLine());

        // list all ips
        Basic.displayInterfaceInformation(port);
        System.out.print("Input your selected IP: ");
        this.myip = this.scan.nextLine().trim();

        // create your own RMI for Service to ping to
        Registry serviceRegistry;
        try {
            serviceRegistry = LocateRegistry.createRegistry(port);
            SubRMIInterface sri;
            sri = (SubRMIInterface) UnicastRemoteObject.exportObject(this, 0);
            serviceRegistry.rebind("SUBSCRIBER", sri);
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }

        // connect with service
        while (this.service_stub == null) {
            try {
                System.out.print("Input " + Basic.TITLE + " ip:port ");
                String server_ip = s.nextLine();// = "127.0.0.1:911";//
                Registry registry = LocateRegistry.getRegistry(server_ip.split(":")[0],
                        Integer.parseInt(server_ip.split(":")[1]));
                System.out.println("Connected to " + Basic.TITLE);

                // get client manager instance
                this.service_stub = (ServiceRMIInterface) registry.lookup("BULLETIN_BOARD_SERVICE");
            } catch (Exception e) {
                System.err.println("Invalid " + Basic.TITLE + " details! try again. " + e.toString());
            }
        }

    }

    @Override
    public void menu() {

        while (true) {

            try {
                boolean break_loop = false;
                System.out.print(
                        " [1]-Subscribe to Topic\n [2]-Unsubscribe to Topic\n [3]-Subscription list\n [4]-Back\n choice: ");
                int choice = Integer.parseInt(this.scan.nextLine());

                switch (choice) {
                    case 1:
                        subscribeAction();
                        break;
                    case 2:
                        unsubscribeAction();
                        break;
                    case 3:
                        System.out.println("  Subscribed to:");
                        subscription_list.forEach((t) -> System.out.println("   " + t));
                        break;
                    case 4:
                        break_loop = true;
                        break;
                    default:
                        throw new Exception("Invalid Publisher input");
                }

                if (break_loop)
                    break;
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    void subscribeAction() {
        Topic temp_topic1 = null;
        while (temp_topic1 == null) {
            try {
                int i = -1;
                System.out.println("List of topics: ");
                for (Topic temp_topic : Topic.values()) {
                    i++;
                    // exclude topic that we have already subscribed to
                    if (this.subscription_list.contains(temp_topic))
                        continue;
                    System.out.println("[" + i + "] " + temp_topic);
                }
                System.out.print("Enter Topic id to subscribe: ");
                int topic_selected = Integer.parseInt(this.scan.nextLine());
                temp_topic1 = Topic.values()[topic_selected];
            } catch (Exception e) {
                System.err.println("Invalid topic choice! try again");
            }
        }
        try {
            this.service_stub.subscribe(temp_topic1, this.myip);
            this.subscription_list.add(temp_topic1);
        } catch (RemoteException e) {
            System.err.println("Error while subscribing to " + temp_topic1);
        }
    }

    void unsubscribeAction() {
        Topic temp_topic1 = null;
        while (temp_topic1 == null) {
            try {
                int i = -1;
                System.out.print("List of topics: ");
                for (Topic temp_topic : Topic.values()) {
                    i++;
                    // only include topic that we have already subscribed to
                    if (this.subscription_list.contains(temp_topic))
                        System.out.println("[" + i + "] " + temp_topic);
                }
                System.out.print("Enter Topic id to unsubscribe: ");
                int topic_selected = Integer.parseInt(this.scan.nextLine());
                temp_topic1 = Topic.values()[topic_selected];
            } catch (Exception e) {
                System.err.println("Invalid topic choice! try again");
            }
        }
        try {
            this.service_stub.unSubscribe(temp_topic1, this.myip);
            this.subscription_list.remove(temp_topic1);
        } catch (RemoteException e) {
            System.err.println("Error while subscribing to " + temp_topic1);
        }
    }

    @Override
    public void onPublish(Message message) throws RemoteException {
        System.out.println("\n" + message);
    }

}
