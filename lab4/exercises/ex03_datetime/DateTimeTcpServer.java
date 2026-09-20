package lab4.exercises.ex03_datetime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeTcpServer {
    public static final int PORT = 5003;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static void main(String[] args) {
        System.out.println(">>> Đang khởi động DateTime TCP Server trên cổng " + PORT + "...");
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server sẵn sàng chờ kết nối...");
            while (true) {
                try (Socket socket = server.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                     PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

                    System.out.println("[TCP Client kết nối]: " + socket.getRemoteSocketAddress());
                    String command;
                    while ((command = in.readLine()) != null) {
                        String response = processCommand(command);
                        out.println(response);
                        if (command.trim().equalsIgnoreCase("QUIT")) {
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Lỗi client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi ServerSocket: " + e.getMessage());
        }
    }

    public static String processCommand(String command) {
        String cmd = command.trim().toUpperCase();
        LocalDateTime now = LocalDateTime.now();
        return switch (cmd) {
            case "DATE" -> "OK " + now.format(DATE_FMT);
            case "TIME" -> "OK " + now.format(TIME_FMT);
            case "DATETIME" -> "OK " + now.format(DATETIME_FMT);
            case "QUIT" -> "OK BYE";
            default -> "ERR UNKNOWN_COMMAND";
        };
    }
}

