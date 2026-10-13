import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/** Xử lý riêng một kết nối client trên một luồng. */
public class ClientThread implements Runnable {
    private final Socket socket;
    private final Server server;
    private PrintWriter out;
    private String username;
    private Server.Room currentRoom;

    public ClientThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try (Socket client = socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8))) {
            out = new PrintWriter(client.getOutputStream(), true, StandardCharsets.UTF_8);
            send("WELCOME");
            String line;
            while ((line = in.readLine()) != null) {
                if (line.equals("/exit")) break;
                if (line.startsWith("/name ")) setName(line.substring(6).trim());
                else if (line.equals("/rooms")) send(server.roomList());
                else if (line.startsWith("/join ")) joinRoom(line.substring(6).trim());
                else if (line.equals("/quit")) leaveRoom(true);
                else if (line.equals("/list")) listMembers();
                else if (line.startsWith("/pm ")) privateMessage(line.substring(4));
                else if (line.startsWith("/msg ")) sendMessage(line.substring(5));
                else if (!line.isEmpty()) send("ERROR Lệnh không hợp lệ.");
            }
        } catch (IOException e) {
            System.out.println("Client ngắt kết nối: " + e.getMessage());
        } finally {
            leaveRoom(false);
        }
    }

    private void setName(String name) {
        if (name.isEmpty()) send("ERROR Tên không được để trống.");
        else {
            username = name;
            send("OK Đã đặt tên: " + username);
        }
    }

    private void joinRoom(String name) {
        if (username == null) { send("ERROR Hãy nhập tên trước."); return; }
        if (name.isEmpty()) { send("ERROR Tên phòng không được để trống."); return; }
        leaveRoom(false);
        currentRoom = server.findOrCreateRoom(name);
        currentRoom.add(this);
        send("JOINED " + name);
        currentRoom.broadcast("[Hệ thống] " + username + " đã vào phòng.", this);
    }

    private void leaveRoom(boolean notifyClient) {
        Server.Room room = currentRoom;
        if (room == null) return;
        currentRoom = null;
        room.remove(this);
        room.broadcast("[Hệ thống] " + username + " đã rời phòng.", this);
        if (notifyClient) {
            send("LEFT");
            send(server.roomList());
        }
    }

    private void listMembers() {
        if (currentRoom == null) { send("ERROR Bạn chưa vào phòng."); return; }
        send("MEMBERS " + currentRoom.memberNames());
    }

    private void privateMessage(String command) {
        if (currentRoom == null) { send("ERROR Bạn chưa vào phòng."); return; }
        String[] parts = command.split("\\s+", 2);
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            send("ERROR Cú pháp: /pm <tên_user> <nội_dung>");
            return;
        }
        ClientThread recipient = currentRoom.findMember(parts[0]);
        if (recipient == null) { send("ERROR Không tìm thấy người dùng trong phòng."); return; }
        recipient.send("MESSAGE [Tin nhắn riêng từ " + username + "] " + parts[1]);
        send("INFO Đã gửi tin nhắn riêng cho " + parts[0] + ".");
    }

    private void sendMessage(String message) {
        if (currentRoom == null) { send("ERROR Bạn chưa vào phòng."); return; }
        if (!message.trim().isEmpty()) currentRoom.broadcast("[" + username + "] " + message, this);
    }

    String getUsername() { return username; }
    void send(String message) { out.println(message); }
}
