package pkg.pub;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import pkg.comm.Basic;
import pkg.comm.MenuClass;
import pkg.comm.Message;
import pkg.comm.Basic.Topic;
import pkg.registry.ServiceRMIInterface;

public class PatelP3Publisher extends MenuClass {
    private Topic topic;
    private ServiceRMIInterface service_stub;

    public PatelP3Publisher(Scanner s, String name) {
        super(s, name);

        while (this.topic == null) {
            try {
                int i = 0;
                for (Topic temp_topic : Topic.values()) {
                    System.out.println("[" + (i++) + "] " + temp_topic);
                }
                System.out.print("Topic of publication: ");
                int topic_selected = Integer.parseInt(this.scan.nextLine());
                this.topic = Topic.values()[topic_selected];
                // System.out.println("topic selected " + this.topic);
            } catch (Exception e) {
                System.err.println("Invalid topic choice! try again");
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
    }

    @Override
    public String toString() {
        String represent = "<" + name + ">\n";

        represent += "Topic: " + topic + "\n";
        return represent;
    }

    @Override
    public void menu() {
        while (true) {
            try {
                boolean break_loop = false;
                System.out.print(" [1]Publish\n [2]Exit\n choice: ");
                int choice = Integer.parseInt(this.scan.nextLine());

                switch (choice) {
                    case 1:
                        System.out.print("Message to publish: ");
                        String msg = this.scan.nextLine();
                        this.service_stub.publish(new Message(this.topic, this.name, msg));
                        break;

                    case 2:
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
}
