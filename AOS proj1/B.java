import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class B {
    public static void main(String[] sr) throws UnknownHostException, IOException {
        Socket s = new Socket("localhost", 81);
        DataInputStream dis = new DataInputStream(s.getInputStream());
        System.out.println(dis.readUTF());
        System.out.println("B end");
    }
}
