import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class C {
    public static void main(String[] sr) throws UnknownHostException, IOException {
        Socket s = new Socket("localhost", 81);
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        dos.writeUTF("hi");
        System.out.println("C end");
    }
}
