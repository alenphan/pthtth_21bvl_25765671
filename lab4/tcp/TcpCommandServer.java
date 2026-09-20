package lab4.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Locale;

public class TcpCommandServer {
    public static final int PORT = 50000;

    public static void main(String[] args) {
        System.out.println(">>> Đang khởi động TCP Server trên cổng " + PORT + "...");

        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("TCP Server đang lắng nghe trên cổng " + PORT + " (chờ client kết nối)...");

            while (true) {

                try (Socket socket = server.accept()) {
                    System.out.println("Đã chấp nhận kết nối từ: " + socket.getRemoteSocketAddress());
                    serve(socket);
                } catch (IOException e) {
                    System.err.println("Lỗi phiên kết nối client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Không thể mở ServerSocket: " + e.getMessage());
        }
    }

    public static void serve(Socket socket) throws IOException {

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String request;

            while ((request = in.readLine()) != null) {
                System.out.println("[Nhận từ " + socket.getRemoteSocketAddress() + "]: " + request);
                String response = process(request);
                out.println(response);

                if (request.trim().equalsIgnoreCase("QUIT")) {
                    break;
                }
            }
        }
    }

    public static String process(String request) {
        String trimmed = request.trim();
        if (trimmed.equalsIgnoreCase("PING")) {
            return "OK PONG";
        }
        if (trimmed.equalsIgnoreCase("TIME")) {
            return "OK " + LocalDateTime.now();
        }
        if (trimmed.equalsIgnoreCase("QUIT")) {
            return "OK BYE";
        }
        if (trimmed.regionMatches(true, 0, "UPPER ", 0, 6)) {
            String content = trimmed.substring(6);
            return "OK " + content.toUpperCase(Locale.ROOT);
        }
        return "ERR UNKNOWN_COMMAND";
    }
}

