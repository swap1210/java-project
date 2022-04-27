package pkg.broker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pkg.comm.Basic;
import pkg.comm.MenuClass;
import pkg.comm.Message;
import pkg.comm.Basic.Topic;
import pkg.registry.ServiceRMIInterface;
import pkg.registry.SubRMIInterface;

public class PatelP3Service extends MenuClass implements ServiceRMIInterface {

    private ExecutorService es = Executors.newCachedThreadPool();
    private Map<String, AuthCred> AUTH_LIST = new HashMap<String, AuthCred>();
    private Map<Basic.Topic, List<Message>> publication_list;
    private Map<Basic.Topic, List<SubRMIInterface>> sub_list;

    public PatelP3Service(Scanner scan, String name) throws IOException {
        super(scan, name);

        String ms_ip = InetAddress.getLocalHost().toString().split("/")[1];
        System.out.print("Enter New Bulletin Board Service port: ");
        int port = 911;// Integer.parseInt(scan.nextLine());
        System.out.println("Port selected " + port);
        System.out.println("Enter Bulletin Board Service IP " + ms_ip + ":" + port);
        Registry serviceRegistry = LocateRegistry.createRegistry(port);
        ServiceRMIInterface sri;
        sri = (ServiceRMIInterface) UnicastRemoteObject.exportObject(this, 0);
        serviceRegistry.rebind("BULLETIN_BOARD_SERVICE", sri);
        publication_list = new HashMap<Basic.Topic, List<Message>>();
        sub_list = new HashMap<Basic.Topic, List<SubRMIInterface>>();
        loadAuthList();
    }

    public void menu() {
        System.out.println("Welcome to Bulletin Board Services: ");
        String choice;
        while (true) {
            System.out.print("[1]List Publishers\n[2]List Subscribers\n[3]Back\nChoice: ");
            choice = this.scan.nextLine();
            boolean exit_flag = false;
            switch (choice.charAt(0)) {
                case '1':
                    System.out.println("Publishers:");
                    publication_list.forEach((t, p) -> System.out.println(p));
                    break;
                case '2':
                    System.out.println("Subscribers:");
                    sub_list.forEach((t, s) -> System.out.println(s));
                    break;
                case '3':
                    es.shutdown();
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
                AUTH_LIST.put(user, new AuthCred(nodeType, user, pass));
            }
            authScan.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publish(Message message) throws RemoteException {

        // check if topic exists
        if (publication_list.containsKey(message.getTopic())) {
            LinkedList<Message> currentPublicationList = (LinkedList<Message>) publication_list.get(message.getTopic());
            currentPublicationList.addFirst(message);
        } else {
            List<Message> currentPublicationList = new LinkedList<Message>();
            currentPublicationList.add(message);
            publication_list.put(message.getTopic(), currentPublicationList);
        }
    }

    @Override
    public void subscribe(Topic topic) throws RemoteException {
        // TODO Auto-generated method stub

    }
}
