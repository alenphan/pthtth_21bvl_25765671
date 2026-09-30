package lab4.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpCommandClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = 50000;
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Lỗi: Port phải là số nguyên!");
                return;
            }
        }

        System.out.println(">>> Đang kết nối tới TCP Server " + host + ":" + port + "...");

        try (Socket socket = new Socket(host, port);

             BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            System.out.println("Kết nối thành công! Nhập các lệnh: PING, TIME, UPPER <nội dung>, QUIT");

            String request;
            System.out.print("> ");
            while ((request = console.readLine()) != null) {
                if (request.trim().isEmpty()) {
                    System.out.print("> ");
                    continue;
                }
                out.println(request);

                String response = in.readLine();
                if (response == null) {
                    System.out.println("Server đã đóng kết nối.");
                    break;
                }
                System.out.println("Server phản hồi: " + response);

                if (request.trim().equalsIgnoreCase("QUIT")) {
                    System.out.println("Đã ngắt kết nối với server. Tạm biệt!");
                    break;
                }
                System.out.print("> ");
            }
        } catch (IOException e) {
            System.err.println("Lỗi kết nối Socket: " + e.getMessage());
        }
    }
}

