package pkg.broker;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import pkg.comm.Basic;
import pkg.comm.MenuClass;
import pkg.comm.Message;
import pkg.comm.Basic.Topic;
import pkg.registry.ServiceRMIInterface;
import pkg.registry.SubRMIInterface;

public class PatelP3Service extends MenuClass implements ServiceRMIInterface {

    private Map<Basic.Topic, List<Message>> publication_map;
    // Map of all the topics and each subscribers in the map are mapped to their
    // respective ip addresses so it's convinent to remove them
    private Map<Basic.Topic, List<String>> sub_ip_list_map;
    private Map<String, SubRMIInterface> sub_ip_stub;

    public PatelP3Service(Scanner scan, String name) throws IOException {
        super(scan, name);

        System.out.print("Enter New " + Basic.TITLE + " port: ");
        int port = Integer.parseInt(scan.nextLine());// = 911;//
        Basic.displayInterfaceInformation(port);
        Registry serviceRegistry = LocateRegistry.createRegistry(port);
        ServiceRMIInterface sri;
        sri = (ServiceRMIInterface) UnicastRemoteObject.exportObject(this, 0);
        serviceRegistry.rebind("BULLETIN_BOARD_SERVICE", sri);
        publication_map = new HashMap<Basic.Topic, List<Message>>();
        this.sub_ip_list_map = new HashMap<Basic.Topic, List<String>>();
        this.sub_ip_stub = new HashMap<String, SubRMIInterface>();
    }

    public void menu() {
        System.out.println(" Welcome to " + Basic.TITLE + "s: ");
        String choice;
        while (true) {
            System.out.print(" [1]-List Publications\n [2]-List Subscribers\n [3]-Back\n Choice: ");
            choice = this.scan.nextLine();
            boolean exit_flag = false;
            switch (choice.charAt(0)) {
                case '1':
                    System.out.println(" Publications:");
                    publication_map.forEach((t, p) -> {
                        System.out.println("  " + t + ":");
                        p.forEach(m -> System.out.println("   " + m));
                    });
                    break;
                case '2':
                    System.out.println(" Subscribers:");
                    for (Topic topics : this.sub_ip_list_map.keySet()) {
                        System.out.println("  [" + topics + "]:");
                        this.sub_ip_list_map.get(topics).forEach((stub_ip) -> System.out.println("   " + stub_ip));
                    }
                    break;
                case '3':
                    exit_flag = true;
                default:
                    System.err.println("Invalid input! please try again.");
            }

            if (exit_flag) {
                break;
            }
        }
    }

    @Override
    public void publish(Message message) throws RemoteException {

        Topic topic_key = message.getTopic();
        // check if topic exists
        if (publication_map.containsKey(topic_key)) {
            LinkedList<Message> currentPublicationList = (LinkedList<Message>) publication_map.get(topic_key);
            currentPublicationList.addFirst(message);
        } else {
            List<Message> currentPublicationList = new LinkedList<Message>();
            currentPublicationList.add(message);
            publication_map.put(topic_key, currentPublicationList);
        }

        List<String> temp_sub_list = this.sub_ip_list_map.get(topic_key);
        if (temp_sub_list != null) {
            for (String sub_key : temp_sub_list) {
                this.sub_ip_stub.get(sub_key).onPublish(message);
            }
        }
    }

    @Override
    public void subscribe(Topic topic, String subscriber_addr) throws RemoteException {
        SubRMIInterface temp_sub_stub;
        try {
            Registry registry = LocateRegistry.getRegistry(subscriber_addr.split(":")[0],
                    Integer.parseInt(subscriber_addr.split(":")[1]));

            // get client manager instance
            temp_sub_stub = (SubRMIInterface) registry.lookup("SUBSCRIBER");
            List<String> temp_sub_list = this.sub_ip_list_map.get(topic);
            if (temp_sub_list == null) {
                temp_sub_list = new ArrayList<String>();
                this.sub_ip_list_map.put(topic, temp_sub_list);
            }
            temp_sub_list.add(subscriber_addr);
            if (!this.sub_ip_stub.containsKey(subscriber_addr)) {
                this.sub_ip_stub.put(subscriber_addr, temp_sub_stub);
            }
        } catch (Exception e) {
            System.err.println("Invalid subscriber[" + subscriber_addr + "] details! try again. " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void unSubscribe(Topic topic, String subscriber_addr) throws RemoteException {
        try {
            // this.sub_map.get(topic).remove(subscriber_addr);

            // remove from key from topic list
            List<String> temp_sub_list = this.sub_ip_list_map.get(topic);
            temp_sub_list.remove(subscriber_addr);

            // check if no key exists in Topics then remove from stub map
            boolean found_flag = false;
            for (Topic topics : this.sub_ip_list_map.keySet()) {
                found_flag = this.sub_ip_list_map.get(topics).contains(subscriber_addr);

                if (found_flag)
                    break;
            }

            if (!found_flag) {
                System.out.println("No reference in any topics so removing from stub list");
                this.sub_ip_stub.remove(subscriber_addr);
            }
        } catch (Exception e) {
            System.err.println("unable to unsubscribe " + subscriber_addr + " from " + topic + " because");
            e.printStackTrace();
        }
    }

    @Override
    public List<Message> getAllPublications(Topic topic) throws RemoteException {
        return this.publication_map.get(topic);
    }
}
