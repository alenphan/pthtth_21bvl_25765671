import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class t_Processing extends Thread {
    Socket socket;

    public t_Processing(Socket s) {
        socket = s;
    }

    public void run() {
        try (Socket client = socket;
             DataInputStream dis = new DataInputStream(client.getInputStream());
             DataOutputStream dos = new DataOutputStream(client.getOutputStream())) {

            dos.writeUTF("clientName");
            dos.flush();

            for (char i = '0'; i <= '9'; i++) {
                dos.writeByte(i);
                dos.flush();

                int ch = dis.readUnsignedByte();
                System.out.println("Server phan hoi: " + (char) ch);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        } catch (java.io.IOException e) {
            System.err.println("TCP connection error: " + e.getMessage());
        }
    }
}
