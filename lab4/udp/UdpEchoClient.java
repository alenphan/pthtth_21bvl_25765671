package lab4.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class UdpEchoClient {
    public static final int TIMEOUT_MS = 3000;
    public static final int BUFFER_SIZE = 4096;

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = 50001;
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Lỗi: Port phải là số nguyên!");
                return;
            }
        }
        String message = args.length > 2 ? args[2] : "xin chào UDP tiếng Việt";

        try {
            InetAddress serverAddress = InetAddress.getByName(host);
            byte[] sendData = message.getBytes(StandardCharsets.UTF_8);

            try (DatagramSocket socket = new DatagramSocket()) {

                socket.setSoTimeout(TIMEOUT_MS);

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, port);
                socket.send(sendPacket);
                System.out.println("Đã gửi tin nhắn đến UDP Server " + host + ":" + port + " -> \"" + message + "\"");

                byte[] receiveBuffer = new byte[BUFFER_SIZE];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                try {
                    socket.receive(receivePacket);
                    String receivedText = new String(
                            receivePacket.getData(),
                            receivePacket.getOffset(),
                            receivePacket.getLength(),
                            StandardCharsets.UTF_8);
                    System.out.println("Nhận phản hồi từ Server: " + receivedText);
                } catch (SocketTimeoutException e) {
                    System.err.println("Timeout: Hết " + (TIMEOUT_MS / 1000) + " giây nhưng không nhận được phản hồi từ Server (Gói tin có thể bị rơi hoặc Server chưa bật)!");
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi gửi/nhận UDP: " + e.getMessage());
        }
    }
}

