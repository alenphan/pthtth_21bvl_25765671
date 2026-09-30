package lab4.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class UdpEchoServer {
    public static final int PORT = 50001;
    public static final int BUFFER_SIZE = 4096;

    public static void main(String[] args) {
        byte[] buffer = new byte[BUFFER_SIZE];

        System.out.println(">>> Đang khởi động UDP Echo Server trên cổng " + PORT + "...");
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("UDP Server đang lắng nghe gói tin trên cổng " + PORT + "...");

            while (true) {

                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                String message = new String(request.getData(), request.getOffset(), request.getLength(), StandardCharsets.UTF_8);
                System.out.println("[Nhận từ " + request.getAddress() + ":" + request.getPort() + "]: " + message);

                String responseText = "ACK " + message.toUpperCase(Locale.ROOT);
                byte[] responseBytes = responseText.getBytes(StandardCharsets.UTF_8);

                DatagramPacket response = new DatagramPacket(
                        responseBytes, responseBytes.length,
                        request.getAddress(), request.getPort());

                socket.send(response);
            }
        } catch (IOException e) {
            System.err.println("Lỗi UDP Server: " + e.getMessage());
        }
    }
}

