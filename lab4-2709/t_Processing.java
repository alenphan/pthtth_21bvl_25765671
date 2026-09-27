import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class t_Processing extends Thread {
    Socket socket;

    public t_Processing(Socket s) {
        socket = s;
    }

    public void run() {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            dos.writeUTF("clientName");
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