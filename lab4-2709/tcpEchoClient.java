import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class tcpEchoClient {
    public static void main(String[] args) throws InterruptedException {
        //socket = new Socket("localhost", 6789); // "127.0.0.x"
        try {
            Socket socket = new Socket(args[0], Integer.parseInt(args[1]));

            System.out.println("CLIENT da duoc tao");

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            String clientName = dis.readUTF();
            dos.flush();

            for (char i = '0'; i <= '9'; i++) {
                dos.writeByte(i);
                dos.flush();

                int ch = dis.readUnsignedByte();
                System.out.println("Server phan hoi: " + (char) ch);

                Thread.sleep(2000);
            }

            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}