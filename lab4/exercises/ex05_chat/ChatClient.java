package lab4.exercises.ex05_chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ChatClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5006;

        System.out.println(">>> Đang kết nối tới Chat Server (" + host + ":" + port + ")...");
        try {
            Socket socket = new Socket(host, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

            Thread receiverThread = new Thread(() -> {
                try {
                    String serverMsg;
                    while ((serverMsg = in.readLine()) != null) {
                        System.out.println("\n" + serverMsg);
                        System.out.print("> ");
                    }
                } catch (IOException e) {
                    System.out.println("\n[Đã ngắt kết nối với Chat Server]");
                }
            });
            receiverThread.setDaemon(true);
            receiverThread.start();

            String userLine;
            while ((userLine = console.readLine()) != null) {
                out.println(userLine);
                if (userLine.trim().equalsIgnoreCase("QUIT")) {
                    break;
                }
            }

            socket.close();
            System.out.println("Tạm biệt!");
        } catch (IOException e) {
            System.err.println("Lỗi kết nối: " + e.getMessage());
        }
    }
}

