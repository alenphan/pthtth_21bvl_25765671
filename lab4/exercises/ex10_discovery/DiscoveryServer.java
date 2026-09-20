package lab4.exercises.ex10_discovery;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class DiscoveryServer {
    public static final int UDP_DISCOVERY_PORT = 5012;
    public static final int TCP_SERVICE_PORT = 5013;
    public static final String SERVICE_NAME = "IUH_Banking_Service";
    public static final String SERVICE_VERSION = "v1.0";

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("BÀI 10: SERVER UDP DISCOVERY + DỊCH VỤ TCP");
        System.out.println("===============================================================");

        Thread discoveryThread = new Thread(DiscoveryServer::runUdpDiscovery);
        discoveryThread.setDaemon(true);
        discoveryThread.start();

        runTcpService();
    }

    private static void runUdpDiscovery() {
        byte[] buf = new byte[1024];
        try (DatagramSocket socket = new DatagramSocket(UDP_DISCOVERY_PORT)) {
            System.out.println("[UDP Discovery] Đang lắng nghe yêu cầu tìm kiếm trên cổng " + UDP_DISCOVERY_PORT);

            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String msg = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8).trim();
                System.out.println("[UDP Discovery] Nhận yêu cầu: '" + msg + "' từ " + packet.getAddress() + ":" + packet.getPort());

                if (msg.equalsIgnoreCase("DISCOVER_SERVICE")) {
                    String response = "SERVICE " + SERVICE_NAME + " " + TCP_SERVICE_PORT + " " + SERVICE_VERSION;
                    byte[] respData = response.getBytes(StandardCharsets.UTF_8);
                    DatagramPacket respPacket = new DatagramPacket(respData, respData.length, packet.getAddress(), packet.getPort());
                    socket.send(respPacket);
                    System.out.println("[UDP Discovery] Đã phản hồi thông tin TCP Port " + TCP_SERVICE_PORT);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi Discovery: " + e.getMessage());
        }
    }

    private static void runTcpService() {
        try (ServerSocket server = new ServerSocket(TCP_SERVICE_PORT)) {
            System.out.println("[TCP Service] Dịch vụ chính đang chạy trên cổng " + TCP_SERVICE_PORT);

            while (true) {
                Socket client = server.accept();
                new Thread(() -> {
                    try (client;
                         BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
                         PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8), true)) {

                        out.println("CHÀO MỪNG ĐẾN VỚI " + SERVICE_NAME + " (" + SERVICE_VERSION + ")!");
                        out.println("Nhập 'BALANCE' để xem số dư hoặc 'QUIT' để thoát:");

                        String line;
                        while ((line = in.readLine()) != null) {
                            if (line.equalsIgnoreCase("QUIT")) {
                                out.println("OK BYE");
                                break;
                            } else if (line.equalsIgnoreCase("BALANCE")) {
                                out.println("OK SỐ DƯ: 50,000,000 VND");
                            } else {
                                out.println("ERR UNKNOWN_COMMAND");
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Lỗi TCP Client: " + e.getMessage());
                    }
                }).start();
            }
        } catch (Exception e) {
            System.err.println("Lỗi TCP Service: " + e.getMessage());
        }
    }
}

