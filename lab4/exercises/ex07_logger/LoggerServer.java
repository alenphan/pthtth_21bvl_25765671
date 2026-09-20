package lab4.exercises.ex07_logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class LoggerServer {
    public static final int PORT = 5009;
    private static final String LOG_DIR = "lab4/data/logs";
    private static final Pattern CLIENT_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {

        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        System.out.println(">>> Đang khởi động Message Logger Server trên cổng " + PORT + "...");
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server sẵn sàng ghi log vào thư mục: " + dir.getAbsolutePath());

            while (true) {
                Socket socket = server.accept();
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("Lỗi Server: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        String remoteAddress = socket.getRemoteSocketAddress().toString();
        try (socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            out.println("WELCOME: Vui lòng gửi lệnh 'HELLO <clientId>' để bắt đầu ghi nhật ký:");

            String firstLine = in.readLine();
            if (firstLine == null) return;

            String trimmed = firstLine.trim();
            if (!trimmed.startsWith("HELLO ")) {
                out.println("ERR INVALID_PROTOCOL: Phải bắt đầu bằng lệnh HELLO <clientId>");
                return;
            }

            String clientId = trimmed.substring(6).trim();
            if (!CLIENT_ID_PATTERN.matcher(clientId).matches()) {
                out.println("ERR INVALID_CLIENT_ID: clientId chỉ được chứa chữ, số, dấu gạch ngang và gạch dưới!");
                return;
            }

            File logFile = new File(LOG_DIR, clientId + ".txt");
            out.println("OK READY: Bắt đầu gửi nội dung cần ghi log. Gõ 'QUIT' để kết thúc.");

            try (PrintWriter fileWriter = new PrintWriter(new FileWriter(logFile, StandardCharsets.UTF_8, true))) {
                String content;
                while ((content = in.readLine()) != null) {
                    if (content.trim().equalsIgnoreCase("QUIT")) {
                        out.println("OK LOG_CLOSED");
                        break;
                    }

                    String logEntry = String.format("[%s] [%s] %s", LocalDateTime.now().format(TIME_FORMAT), remoteAddress, content);
                    fileWriter.println(logEntry);
                    fileWriter.flush();

                    out.println("OK SAVED");
                }
            }

        } catch (IOException e) {
            System.err.println("Lỗi ghi log với client " + remoteAddress + ": " + e.getMessage());
        }
    }
}

