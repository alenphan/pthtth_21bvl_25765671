package lab4.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiClientTcpServer {
    public static final int PORT = 50000;
    public static final int MAX_CLIENTS = 20;

    public static void main(String[] args) {

        ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTS);

        System.out.println(">>> Đang khởi động Multi-Client TCP Server trên cổng " + PORT + "...");
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server sẵn sàng phục vụ đồng thời tối đa " + MAX_CLIENTS + " clients cùng lúc.");

            while (true) {

                Socket socket = server.accept();

                pool.submit(() -> {
                    String clientInfo = String.valueOf(socket.getRemoteSocketAddress());
                    System.out.println("[+] Client kết nối: " + clientInfo + " (Thread: " + Thread.currentThread().getName() + ")");

                    try (socket) {

                        TcpCommandServer.serve(socket);
                    } catch (IOException e) {
                        System.err.println("[-] Lỗi với client " + clientInfo + ": " + e.getMessage());
                    } finally {
                        System.out.println("[-] Client đã ngắt kết nối: " + clientInfo);
                    }
                });
            }
        } catch (IOException e) {
            System.err.println("Lỗi ServerSocket: " + e.getMessage());
        } finally {

            pool.shutdown();
        }
    }
}

