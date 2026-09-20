package lab4.exercises.ex04_calculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class CalcServer {
    public static final int PORT = 5005;

    public static void main(String[] args) {
        System.out.println(">>> Đang khởi động Remote Calculator Server trên cổng " + PORT + "...");
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server sẵn sàng tính toán...");
            while (true) {
                try (Socket socket = server.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                     PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

                    System.out.println("[Client kết nối]: " + socket.getRemoteSocketAddress());
                    String line;
                    while ((line = in.readLine()) != null) {
                        if (line.trim().equalsIgnoreCase("QUIT")) {
                            out.println("OK BYE");
                            break;
                        }
                        String response = calculate(line);
                        out.println(response);
                    }
                } catch (IOException e) {
                    System.err.println("Lỗi client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi Server: " + e.getMessage());
        }
    }

    public static String calculate(String request) {
        String trimmed = request.trim();

        String[] parts = trimmed.split("\\s+");
        if (parts.length != 4 || !parts[0].equalsIgnoreCase("CALC")) {
            return "ERR INVALID_FORMAT";
        }

        String op = parts[1];
        double num1;
        double num2;

        try {
            num1 = Double.parseDouble(parts[2]);
            num2 = Double.parseDouble(parts[3]);
        } catch (NumberFormatException e) {
            return "ERR INVALID_NUMBER";
        }

        double result;
        switch (op) {
            case "+":
                result = num1 + num2;
                break;
            case "-":
                result = num1 - num2;
                break;
            case "*":
                result = num1 * num2;
                break;
            case "/":
                if (num2 == 0.0) {
                    return "ERR DIVIDE_BY_ZERO";
                }
                result = num1 / num2;
                break;
            default:
                return "ERR UNSUPPORTED_OPERATOR";
        }

        if (result == (long) result) {
            return "OK " + (long) result;
        } else {
            return "OK " + result;
        }
    }
}

