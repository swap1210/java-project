import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class A {
    public static void main(String[] s) throws IOException {
        ServerSocket ss = new ServerSocket(81);
        Socket b, c = null;

        b = ss.accept();

        c = ss.accept();

        DataInputStream dis_b = new DataInputStream(b.getInputStream());
        DataOutputStream dos_b = new DataOutputStream(b.getOutputStream());
        DataInputStream dis_c = new DataInputStream(c.getInputStream());
        DataOutputStream dos_c = new DataOutputStream(c.getOutputStream());

        dos_c.writeUTF(dis_b.readUTF());
        System.out.println("A end");
    }
}
