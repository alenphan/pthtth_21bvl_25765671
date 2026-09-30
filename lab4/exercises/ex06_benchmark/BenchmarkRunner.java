package lab4.exercises.ex06_benchmark;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class BenchmarkRunner {
    private static final int TCP_PORT = 5007;
    private static final int UDP_PORT = 5008;
    private static final int MESSAGE_COUNT = 1000;
    private static final int RUN_ROUNDS = 5;

    public static void main(String[] args) throws Exception {
        System.out.println("===============================================================");
        System.out.println("BÀI 6: THỰC NGHIỆM ĐO HIỆU NĂNG TCP VÀ UDP (" + MESSAGE_COUNT + " GÓI TIN / LẦN)");
        System.out.println("===============================================================");

        Thread tcpServerThread = new Thread(BenchmarkRunner::runTcpServer);
        tcpServerThread.setDaemon(true);
        tcpServerThread.start();

        Thread udpServerThread = new Thread(BenchmarkRunner::runUdpServer);
        udpServerThread.setDaemon(true);
        udpServerThread.start();

        Thread.sleep(500);

        long[] tcpTimes = new long[RUN_ROUNDS];
        long[] udpTimes = new long[RUN_ROUNDS];
        int[] udpReceivedCount = new int[RUN_ROUNDS];

        for (int round = 1; round <= RUN_ROUNDS; round++) {
            System.out.println("\n--- Vòng đo thứ " + round + " / " + RUN_ROUNDS + " ---");

            long startTcp = System.currentTimeMillis();
            int tcpOk = testTcp();
            long durationTcp = System.currentTimeMillis() - startTcp;
            tcpTimes[round - 1] = durationTcp;
            System.out.printf("[TCP] Thời gian: %4d ms | Thành công: %d/%d (100%%)%n", durationTcp, tcpOk, MESSAGE_COUNT);

            long startUdp = System.currentTimeMillis();
            int udpOk = testUdp();
            long durationUdp = System.currentTimeMillis() - startUdp;
            udpTimes[round - 1] = durationUdp;
            udpReceivedCount[round - 1] = udpOk;
            double lossRate = (1.0 - (double) udpOk / MESSAGE_COUNT) * 100.0;
            System.out.printf("[UDP] Thời gian: %4d ms | Nhận được : %d/%d | Rơi gói: %.1f%%%n", durationUdp, udpOk, MESSAGE_COUNT, lossRate);
        }

        long avgTcp = 0, avgUdp = 0;
        int totalUdpRecv = 0;
        for (int i = 0; i < RUN_ROUNDS; i++) {
            avgTcp += tcpTimes[i];
            avgUdp += udpTimes[i];
            totalUdpRecv += udpReceivedCount[i];
        }
        avgTcp /= RUN_ROUNDS;
        avgUdp /= RUN_ROUNDS;

        System.out.println("\n===============================================================");
        System.out.println("TỔNG KẾT SAU " + RUN_ROUNDS + " LẦN ĐO:");
        System.out.println("- Thời gian trung bình TCP : " + avgTcp + " ms (Độ tin cậy: 100%)");
        System.out.println("- Thời gian trung bình UDP : " + avgUdp + " ms (Tỷ lệ nhận trung bình: " + ((double) totalUdpRecv / (RUN_ROUNDS * MESSAGE_COUNT) * 100) + "%)");
        System.out.println("NHẬN XÉT HỌC TẬP:");
        System.out.println("1. TCP thiết lập kết nối trước, bảo đảm đúng thứ tự và không mất gói tin nhờ cơ chế ACK/retransmit.");
        System.out.println("2. UDP gửi trực tiếp không cần bắt tay 3 bước, overhead thấp hơn nhưng không bảo đảm gói tin đến nơi.");
        System.out.println("===============================================================");
    }

    private static int testTcp() {
        int count = 0;
        try (Socket socket = new Socket("localhost", TCP_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            for (int i = 0; i < MESSAGE_COUNT; i++) {
                out.println("MSG_" + i);
                String resp = in.readLine();
                if (resp != null) {
                    count++;
                }
            }
            out.println("END");
        } catch (Exception e) {
            System.err.println("Lỗi test TCP: " + e.getMessage());
        }
        return count;
    }

    private static int testUdp() {
        int count = 0;
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(100);
            InetAddress local = InetAddress.getByName("localhost");
            byte[] recvBuf = new byte[256];

            for (int i = 0; i < MESSAGE_COUNT; i++) {
                byte[] sendData = ("MSG_" + i).getBytes(StandardCharsets.UTF_8);
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, local, UDP_PORT);
                socket.send(sendPacket);

                DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
                try {
                    socket.receive(recvPacket);
                    count++;
                } catch (SocketTimeoutException ignored) {

                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi test UDP: " + e.getMessage());
        }
        return count;
    }

    private static void runTcpServer() {
        try (ServerSocket server = new ServerSocket(TCP_PORT)) {
            while (true) {
                try (Socket client = server.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
                     PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8), true)) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        if (line.equals("END")) break;
                        out.println("ACK_" + line);
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    private static void runUdpServer() {
        byte[] buf = new byte[256];
        try (DatagramSocket socket = new DatagramSocket(UDP_PORT)) {
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                DatagramPacket resp = new DatagramPacket(packet.getData(), packet.getLength(), packet.getAddress(), packet.getPort());
                socket.send(resp);
            }
        } catch (Exception ignored) {}
    }
}

