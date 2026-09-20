package lab4.exercises.ex09_multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;

public class MulticastReceiver {
    public static final String GROUP_IP = "239.255.0.1";
    public static final int PORT = 5011;

    public static void main(String[] args) {
        System.out.println(">>> Đang khởi động Multicast Receiver...");
        try {
            InetAddress group = InetAddress.getByName(GROUP_IP);
            NetworkInterface netIf = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            if (netIf == null) {
                netIf = NetworkInterface.getNetworkInterfaces().nextElement();
            }

            try (MulticastSocket socket = new MulticastSocket(PORT)) {
                InetSocketAddress groupAddress = new InetSocketAddress(group, PORT);

                socket.joinGroup(groupAddress, netIf);
                System.out.println("Đã tham gia nhóm Multicast: " + GROUP_IP + ":" + PORT + " qua card mạng " + netIf.getDisplayName());
                System.out.println("Đang chờ nhận thông báo từ Sender...");

                byte[] buffer = new byte[2048];
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String msg = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
                    System.out.println("[BẢN TIN TỪ " + packet.getAddress() + "]: " + msg);

                    if (msg.contains("LEAVE_GROUP")) {
                        System.out.println("Nhận lệnh rời nhóm!");
                        socket.leaveGroup(groupAddress, netIf);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi Multicast Receiver: " + e.getMessage());
        }
    }
}

