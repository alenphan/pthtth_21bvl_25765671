package lab4.exercises.ex10_discovery;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class DiscoveryClient {
    public static final int UDP_DISCOVERY_PORT = 5012;
    public static final int TIMEOUT_MS = 3000;

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        System.out.println(">>> [BƯỚC 1: UDP DISCOVERY] Đang tìm kiếm dịch vụ tại " + host + ":" + UDP_DISCOVERY_PORT + "...");

        try (DatagramSocket udpSocket = new DatagramSocket()) {
            udpSocket.setSoTimeout(TIMEOUT_MS);
            byte[] sendData = "DISCOVER_SERVICE".getBytes(StandardCharsets.UTF_8);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(host), UDP_DISCOVERY_PORT);
            udpSocket.send(sendPacket);

            byte[] buf = new byte[1024];
            DatagramPacket recvPacket = new DatagramPacket(buf, buf.length);

            try {
                udpSocket.receive(recvPacket);
            } catch (SocketTimeoutException e) {
                System.err.println("Timeout: Không tìm thấy server nào phản hồi sau " + (TIMEOUT_MS / 1000) + "s!");
                return;
            }

            String response = new String(recvPacket.getData(), recvPacket.getOffset(), recvPacket.getLength(), StandardCharsets.UTF_8).trim();
            System.out.println("Đã phát hiện dịch vụ: " + response);

            String[] parts = response.split("\\s+");
            if (parts.length < 4 || !parts[0].equals("SERVICE")) {
                System.err.println("Gói tin phản hồi discovery không đúng định dạng!");
                return;
            }

            String serviceName = parts[1];
            int tcpPort = Integer.parseInt(parts[2]);
            String version = parts[3];
            InetAddress targetServerIp = recvPacket.getAddress();

            System.out.println("\n>>> [BƯỚC 2: KẾT NỐI TCP] Kết nối tới '" + serviceName + "' (" + version + ") tại " + targetServerIp.getHostAddress() + ":" + tcpPort);

            try (Socket tcpSocket = new Socket(targetServerIp, tcpPort);
                 BufferedReader in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream(), StandardCharsets.UTF_8));
                 PrintWriter out = new PrintWriter(new OutputStreamWriter(tcpSocket.getOutputStream(), StandardCharsets.UTF_8), true);
                 BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

                System.out.println("Server: " + in.readLine());
                System.out.println("Server: " + in.readLine());

                String line;
                System.out.print("> ");
                while ((line = console.readLine()) != null) {
                    out.println(line);
                    String serverResp = in.readLine();
                    if (serverResp == null) break;
                    System.out.println("Server: " + serverResp);

                    if (line.trim().equalsIgnoreCase("QUIT")) {
                        break;
                    }
                    System.out.print("> ");
                }
            }

        } catch (Exception e) {
            System.err.println("Lỗi quá trình Discovery & Kết nối: " + e.getMessage());
        }
    }
}

