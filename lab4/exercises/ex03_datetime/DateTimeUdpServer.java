package lab4.exercises.ex03_datetime;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUdpServer {
    public static final int PORT = 5004;
    private static final int BUFFER_SIZE = 1024;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static void main(String[] args) {
        byte[] buffer = new byte[BUFFER_SIZE];
        System.out.println(">>> Đang khởi động DateTime UDP Server trên cổng " + PORT + "...");
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("Server UDP sẵn sàng nhận gói tin...");
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                String cmd = new String(request.getData(), request.getOffset(), request.getLength(), StandardCharsets.UTF_8).trim().toUpperCase();
                System.out.println("[UDP nhận từ " + request.getAddress() + ":" + request.getPort() + "]: " + cmd);

                LocalDateTime now = LocalDateTime.now();
                String responseText = switch (cmd) {
                    case "DATE" -> "OK " + now.format(DATE_FMT);
                    case "TIME" -> "OK " + now.format(TIME_FMT);
                    case "DATETIME" -> "OK " + now.format(DATETIME_FMT);
                    default -> "ERR UNKNOWN_COMMAND";
                };

                byte[] respBytes = responseText.getBytes(StandardCharsets.UTF_8);
                DatagramPacket response = new DatagramPacket(
                        respBytes, respBytes.length,
                        request.getAddress(), request.getPort());
                socket.send(response);
            }
        } catch (IOException e) {
            System.err.println("Lỗi UDP Server: " + e.getMessage());
        }
    }
}

