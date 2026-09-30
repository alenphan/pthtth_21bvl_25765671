package lab4.exercises.ex02_digit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class DigitServer {
    public static final int PORT = 5002;

    private static final String[] DIGIT_WORDS = {
        "Không", "Một", "Hai", "Ba", "Bốn", "Năm", "Sáu", "Bảy", "Tám", "Chín"
    };

    public static void main(String[] args) {
        System.out.println(">>> Đang khởi động Server Đổi Chữ Số trên cổng " + PORT + "...");
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server sẵn sàng trên cổng " + PORT + "...");
            while (true) {
                try (Socket socket = server.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                     PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

                    System.out.println("[Client kết nối]: " + socket.getRemoteSocketAddress());
                    String line;
                    while ((line = in.readLine()) != null) {
                        String trimmed = line.trim();
                        if (trimmed.equalsIgnoreCase("QUIT")) {
                            out.println("OK BYE");
                            break;
                        }

                        String response = convertDigit(line);
                        out.println(response);
                    }
                } catch (IOException e) {
                    System.err.println("Lỗi client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi ServerSocket: " + e.getMessage());
        }
    }

    public static String convertDigit(String input) {

        if (input == null || input.length() != 1) {
            return "ERR INVALID_DIGIT";
        }
        char c = input.charAt(0);
        if (c >= '0' && c <= '9') {
            int digit = c - '0';
            return DIGIT_WORDS[digit];
        }
        return "ERR INVALID_DIGIT";
    }
}

