package lab4.exercises.ex05_chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    public static final int PORT = 5006;
    public static final int MAX_USERS = 50;

    private static final Map<String, PrintWriter> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(MAX_USERS);
        System.out.println(">>> Đang khởi động Chat Server trên cổng " + PORT + "...");

        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Chat Server sẵn sàng!");

            while (true) {
                Socket socket = server.accept();
                pool.submit(() -> handleClient(socket));
            }
        } catch (IOException e) {
            System.err.println("Lỗi Server: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private static void handleClient(Socket socket) {
        String nickname = null;
        try (socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            out.println("CHÀO MỪNG ĐẾN PHÒNG CHAT IUH! Vui lòng nhập Nickname của bạn:");

            while (true) {
                String line = in.readLine();
                if (line == null) return;
                String candidate = line.trim();

                if (candidate.isEmpty()) {
                    out.println("ERR NICKNAME_EMPTY: Nickname không được để trống!");
                    continue;
                }
                if (candidate.contains(" ")) {
                    out.println("ERR NICKNAME_SPACE: Nickname không được chứa khoảng trắng!");
                    continue;
                }

                synchronized (clients) {
                    if (clients.containsKey(candidate)) {
                        out.println("ERR NICKNAME_TAKEN: Nickname '" + candidate + "' đã có người dùng. Vui lòng chọn tên khác:");
                    } else {
                        nickname = candidate;
                        clients.put(nickname, out);
                        break;
                    }
                }
            }

            out.println("OK Xin chào " + nickname + "! Lệnh hỗ trợ: USERS, MSG <nội dung>, QUIT");
            broadcast("[HỆ THỐNG]: Người dùng @" + nickname + " vừa tham gia phòng chat!", nickname);

            String message;
            while ((message = in.readLine()) != null) {
                String trimmed = message.trim();
                if (trimmed.equalsIgnoreCase("QUIT")) {
                    out.println("OK BYE");
                    break;
                } else if (trimmed.equalsIgnoreCase("USERS")) {
                    out.println("DANH SÁCH ONLINE (" + clients.size() + "): " + String.join(", ", clients.keySet()));
                } else if (trimmed.toUpperCase().startsWith("MSG ")) {
                    String content = trimmed.substring(4).trim();
                    broadcast("[" + nickname + "]: " + content, nickname);
                    out.println("OK SENT");
                } else {
                    out.println("ERR UNKNOWN_COMMAND: Cú pháp hợp lệ gồm USERS, MSG <nội dung>, QUIT");
                }
            }
        } catch (IOException e) {
            System.out.println("Người dùng @" + nickname + " bị mất kết nối đột ngột: " + e.getMessage());
        } finally {
            if (nickname != null) {
                clients.remove(nickname);
                broadcast("[HỆ THỐNG]: Người dùng @" + nickname + " đã rời phòng chat.", null);
                System.out.println("[-] Đã gỡ bỏ nickname: " + nickname);
            }
        }
    }

    private static void broadcast(String msg, String excludeNick) {
        System.out.println("[BROADCAST] " + msg);
        for (Map.Entry<String, PrintWriter> entry : clients.entrySet()) {
            if (excludeNick == null || !entry.getKey().equalsIgnoreCase(excludeNick)) {
                entry.getValue().println(msg);
            }
        }
    }
}

