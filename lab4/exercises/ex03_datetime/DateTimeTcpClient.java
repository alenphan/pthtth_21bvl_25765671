package lab4.exercises.ex03_datetime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class DateTimeTcpClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5003;

        System.out.println(">>> Kết nối tới DateTime TCP Server (" + host + ":" + port + ")...");
        try (Socket socket = new Socket(host, port);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            System.out.println("Các lệnh hỗ trợ: DATE, TIME, DATETIME, QUIT");
            String line;
            System.out.print("> ");
            while ((line = console.readLine()) != null) {
                out.println(line);
                String resp = in.readLine();
                if (resp == null) {
                    System.out.println("Server ngắt kết nối.");
                    break;
                }
                System.out.println("Server: " + resp);
                if (line.trim().equalsIgnoreCase("QUIT")) {
                    break;
                }
                System.out.print("> ");
            }
        } catch (IOException e) {
            System.err.println("Lỗi kết nối Socket: " + e.getMessage());
        }
    }
}

