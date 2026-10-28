import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class tcpEchoClient {
    public static void main(String[] args) {
        //socket = new Socket("localhost", 6789); // "127.0.0.x"
        if (args.length < 2) {
            System.err.println("Usage: java tcpEchoClient <host> <port>");
            return;
        }
        try (Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

            System.out.println("CLIENT da duoc tao");

            String clientName = dis.readUTF();
            System.out.println("Connected to: " + clientName);
            dos.flush();

            for (char i = '0'; i <= '9'; i++) {
                dos.writeByte(i);
                dos.flush();

                int ch = dis.readUnsignedByte();
                System.out.println("Server phan hoi: " + (char) ch);

                Thread.sleep(2000);
            }

        } catch (Exception e) {
            System.err.println("TCP client error: " + e.getMessage());
        }
    }
}
