import javax.swing.JFrame;
import java.io.IOException;
import java.net.InetAddress;


public class ClientTest {
    public static void main(String[] args) throws IOException {
        // Computer you are at right now.
        Client greg = new Client("127.0.0.1");
        greg.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        greg.startRunning();
    }
}

