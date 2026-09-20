package lab4.exercises.ex07_logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class LoggerClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5009;
        String clientId = args.length > 2 ? args[2] : "student_21bvl";

        System.out.println(">>> Đang kết nối tới Logger Server (" + host + ":" + port + ") với clientId: " + clientId);
        try (Socket socket = new Socket(host, port);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            System.out.println("Server: " + in.readLine());

            out.println("HELLO " + clientId);
            System.out.println("Server: " + in.readLine());

            System.out.println("Nhập nội dung cần ghi vào nhật ký (hoặc 'QUIT' để thoát):");
            String line;
            System.out.print("> ");
            while ((line = console.readLine()) != null) {
                out.println(line);
                String resp = in.readLine();
                if (resp == null) break;
                System.out.println("Server: " + resp);

                if (line.trim().equalsIgnoreCase("QUIT")) {
                    break;
                }
                System.out.print("> ");
            }
        } catch (Exception e) {
            System.err.println("Lỗi: " + e.getMessage());
        }
    }
}

