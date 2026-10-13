import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client {
    private static final String DISCONNECTED = "__DISCONNECTED__";

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5000;

        try (Socket socket = new Socket(host, port);
             BufferedReader server = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
             BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            System.out.println("Kết nối tới chat server " + host + ":" + port);
            System.out.println("CHÀO MỪNG: Vui lòng nhập TÊN của bạn:");
            String name = keyboard.readLine();
            if (name == null || name.trim().isEmpty()) return;

            BlockingQueue<String> incoming = new LinkedBlockingQueue<>();
            Thread receiver = new Thread(() -> receiveMessages(server, incoming), "server-receiver");
            receiver.setDaemon(true);
            receiver.start();

            out.println("/name " + name.trim());
            String response = waitFor(incoming, "OK ");
            if (response == null || response.startsWith("ERROR ")) {
                System.out.println(response == null ? "Server đã ngắt kết nối." : response);
                return;
            }
            System.out.println("Đã đăng nhập: " + name.trim());

            boolean connected = true;
            while (connected) {
                out.println("/rooms");
                String roomResponse = waitFor(incoming, "ROOMS ");
                if (roomResponse == null) break;
                List<String> roomNames = printRooms(roomResponse.substring(6));
                System.out.println("Nhập TÊN PHÒNG hoặc SỐ THỨ TỰ để tham gia (tự nhập tên để tạo phòng mới).");
                System.out.print("Phòng: ");
                String choice = keyboard.readLine();
                if (choice == null) break;
                choice = choice.trim();
                if (choice.equalsIgnoreCase("/exit")) {
                    out.println("/exit");
                    break;
                }
                String room = chooseRoom(choice, roomNames);
                out.println("/join " + room);
                String joined = waitFor(incoming, "JOINED ");
                if (joined == null) break;
                if (joined.startsWith("ERROR ")) {
                    System.out.println(joined);
                    continue;
                }

                System.out.println("\nĐã tham gia thành công phòng: [" + joined.substring(7) + "]");
                printHelp();
                System.out.println("Gõ tin nhắn và nhấn Enter để gửi.");
                boolean inRoom = true;
                while (inRoom) {
                    System.out.print("> ");
                    String input = keyboard.readLine();
                    if (input == null) {
                        out.println("/exit");
                        connected = false;
                        break;
                    }
                    String text = input.trim();
                    if (text.equals("/exit")) {
                        out.println("/exit");
                        connected = false;
                        inRoom = false;
                    } else if (text.equals("/quit")) {
                        out.println("/quit");
                        waitFor(incoming, "LEFT");
                        System.out.println("Đã rời phòng. Chọn phòng khác.");
                        inRoom = false;
                    } else if (text.equals("/help")) {
                        printHelp();
                    } else if (text.equals("/list")) {
                        out.println("/list");
                        String members = waitFor(incoming, "MEMBERS ");
                        if (members != null) System.out.println("Thành viên trong phòng: " + members.substring(8));
                    } else if (text.startsWith("/pm ")) {
                        out.println(input);
                    } else if (text.startsWith("/")) {
                        System.out.println("Lệnh không hợp lệ. Gõ /help để xem hướng dẫn.");
                    } else if (!text.isEmpty()) {
                        out.println("/msg " + input);
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Không kết nối được tới server: " + e.getMessage());
            System.err.println("Hãy chạy Server trước và kiểm tra host/cổng.");
        }
    }

    private static void receiveMessages(BufferedReader server, BlockingQueue<String> incoming) {
        try {
            String line;
            while ((line = server.readLine()) != null) {
                if (line.startsWith("MESSAGE ")) {
                    System.out.println("\n" + line.substring(8));
                    System.out.print("> ");
                }
                incoming.put(line);
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            incoming.offer(DISCONNECTED);
        }
    }

    private static String waitFor(BlockingQueue<String> incoming, String prefix) throws InterruptedException {
        while (true) {
            String line = incoming.take();
            if (DISCONNECTED.equals(line)) return null;
            if (line.startsWith(prefix)) return line;
            if (line.startsWith("ERROR ") || line.startsWith("INFO ")) System.out.println(line);
        }
    }

    private static List<String> printRooms(String data) {
        List<String> rooms = new ArrayList<>();
        System.out.println("\n========================================");
        System.out.println("       DANH SÁCH PHÒNG CHAT HỆ THỐNG");
        System.out.println("========================================");
        String[] entries = data.isEmpty() ? new String[0] : data.split("\\|");
        for (int i = 0; i < entries.length; i++) {
            String entry = entries[i].trim();
            int countStart = entry.indexOf(" (");
            rooms.add(countStart < 0 ? entry : entry.substring(0, countStart));
            System.out.println((i + 1) + ". " + entry);
        }
        return rooms;
    }

    private static String chooseRoom(String choice, List<String> roomNames) {
        try {
            int index = Integer.parseInt(choice) - 1;
            if (index >= 0 && index < roomNames.size()) return roomNames.get(index);
        } catch (NumberFormatException ignored) {
        }
        return choice;
    }

    private static void printHelp() {
        System.out.println("----------------------------------------------");
        System.out.println("DANH SÁCH CÁC CÂU LỆNH HỖ TRỢ:");
        System.out.println("• Gõ tin nhắn bình thường để chat với phòng.");
        System.out.println("• /list                 : Xem thành viên trong phòng");
        System.out.println("• /pm <tên_user> <nội_dung> : Gửi tin nhắn riêng");
        System.out.println("• /help                 : Xem lại hướng dẫn");
        System.out.println("• /quit                 : Rời phòng và chọn phòng khác");
        System.out.println("• /exit                 : Ngắt kết nối và thoát");
        System.out.println("----------------------------------------------");
    }
}
