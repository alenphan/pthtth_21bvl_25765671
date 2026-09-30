import java.net.ServerSocket;
import java.net.Socket;

public class pTcpEchoServer {
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(6789); //bind(); listen();
            System.out.println("SERVER DANG SONG da duoc tao");
            while (true) {
                Socket s = ss.accept();

                t_Processing tp = new t_Processing(s);
                tp.start();

                // s.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}