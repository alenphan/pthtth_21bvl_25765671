package lab4.exercises.ex03_datetime;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class DateTimeUdpClient {
    public static final int TIMEOUT_MS = 3000;

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5004;

        try (DatagramSocket socket = new DatagramSocket();
             Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {

            socket.setSoTimeout(TIMEOUT_MS);
            InetAddress serverAddr = InetAddress.getByName(host);

            System.out.println("Kết nối DateTime UDP (" + host + ":" + port + ")");
            System.out.println("Nhập lệnh: DATE, TIME, DATETIME hoặc 'EXIT' để dừng:");

            while (true) {
                System.out.print("> ");
                String line = scanner.nextLine();
                if (line.equalsIgnoreCase("EXIT")) {
                    break;
                }

                byte[] sendData = line.getBytes(StandardCharsets.UTF_8);
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddr, port);
                socket.send(sendPacket);

                byte[] recvBuffer = new byte[1024];
                DatagramPacket recvPacket = new DatagramPacket(recvBuffer, recvBuffer.length);
                try {
                    socket.receive(recvPacket);
                    String resp = new String(recvPacket.getData(), recvPacket.getOffset(), recvPacket.getLength(), StandardCharsets.UTF_8);
                    System.out.println("Server: " + resp);
                } catch (SocketTimeoutException e) {
                    System.err.println("Timeout: Không nhận được phản hồi từ server sau " + (TIMEOUT_MS / 1000) + "s!");
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi UDP: " + e.getMessage());
        }
    }
}

