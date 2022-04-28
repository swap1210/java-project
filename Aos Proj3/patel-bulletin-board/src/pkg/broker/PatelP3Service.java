package pkg.broker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
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

    private Map<String, AuthCred> AUTH_MAP = new HashMap<String, AuthCred>();
    private Map<Basic.Topic, List<Message>> publication_map;
    // Map of all the topics and each subscribers in the map are mapped to their
    // respective ip addresses so it's convinent to remove them
    private Map<Basic.Topic, Map<String, SubRMIInterface>> sub_map;

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
        sub_map = new HashMap<Basic.Topic, Map<String, SubRMIInterface>>();
        loadAuthList();
    }

    public void menu() {
        System.out.println(" Welcome to " + Basic.TITLE + "s: ");
        String choice;
        while (true) {
            System.out.print(" [1]-List Publishers\n [2]-List Subscribers\n [3]-Back\nChoice: ");
            choice = this.scan.nextLine();
            boolean exit_flag = false;
            switch (choice.charAt(0)) {
                case '1':
                    System.out.println("Publishers:");
                    publication_map.forEach((t, p) -> System.out.println(p));
                    break;
                case '2':
                    System.out.println(" Subscribers: ");
                    for (Topic topic_key : this.sub_map.keySet()) {
                        System.out.println("  " + topic_key + ":");
                        Map<String, SubRMIInterface> temp_sub_map = this.sub_map.get(topic_key);
                        if (temp_sub_map != null) {
                            for (String sub_key : this.sub_map.get(topic_key).keySet()) {
                                System.out.println("   " + sub_key);
                            }
                            System.out.println();
                        }
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

    void loadAuthList() {
        // System.out.println(System.getProperty("user.dir"));
        try (Scanner authScan = new Scanner(new File("./patel-bulletin-board/res/userList.txt"))
                .useDelimiter("[\\r\\n\\|]+")) {
            // scan the authentication file to load all AuthCred object in a map
            while (authScan.hasNext()) {
                String nodeType = authScan.next();
                String user = authScan.next();
                char[] pass = authScan.next().toCharArray();
                AUTH_MAP.put(user, new AuthCred(nodeType, user, pass));
            }
            authScan.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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

        Map<String, SubRMIInterface> temp_sub_map = this.sub_map.get(topic_key);
        if (temp_sub_map != null) {
            for (String sub_key : this.sub_map.get(topic_key).keySet()) {
                this.sub_map.get(topic_key).get(sub_key).onPublish(message);
            }
        }
    }

    @Override
    public void subscribe(Topic topic, String client_ip) throws RemoteException {
        SubRMIInterface temp_sub_stub;
        try {
            Registry registry = LocateRegistry.getRegistry(client_ip.split(":")[0],
                    Integer.parseInt(client_ip.split(":")[1]));

            // get client manager instance
            temp_sub_stub = (SubRMIInterface) registry.lookup("SUBSCRIBER");
            Map<String, SubRMIInterface> temp_sub_map = (HashMap<String, SubRMIInterface>) this.sub_map.get(topic);
            if (temp_sub_map == null) {// create new map if it's null then put to map
                this.sub_map.put(topic, new HashMap<String, SubRMIInterface>());
                temp_sub_map = this.sub_map.get(topic);
            }
            temp_sub_map.put(client_ip, temp_sub_stub);

        } catch (Exception e) {
            System.err.println("Invalid subscriber[" + client_ip + "] details! try again. " + e.toString());
        }
    }

    @Override
    public void unSubscribe(Topic topic, String subscriber_addr) throws RemoteException {
        try {
            this.sub_map.get(topic).remove(subscriber_addr);
        } catch (Exception e) {
            System.err.println("unable to unsubscribe " + subscriber_addr + " from " + topic + " because");
            e.printStackTrace();
        }
    }
}
