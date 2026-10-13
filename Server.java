import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private final Map<String, Room> rooms = new LinkedHashMap<>();
    private final ExecutorService clientPool = Executors.newCachedThreadPool();

    public Server(int port, List<String> roomNames) {
        this.port = port;
        for (String name : roomNames) rooms.put(name, new Room(name));
    }

    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 5000;
        List<String> names = args.length > 1
                ? Arrays.asList(args).subList(1, args.length)
                : Arrays.asList("Study", "Game", "TraiNghiem");
        new Server(port, names).start();
    }

    private void start() throws IOException {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Chat server đang chạy tại cổng " + port + ". Nhấn Ctrl+C để dừng.");
        while (true) clientPool.execute(new ClientThread(server.accept(), this));
        }
    }

    synchronized Room findOrCreateRoom(String name) {
        return rooms.computeIfAbsent(name, Room::new);
    }

    private synchronized List<Room> allRooms() {
        return new ArrayList<>(rooms.values());
    }

    String roomList() {
        List<String> entries = new ArrayList<>();
        for (Room room : allRooms()) entries.add(room.name + " (" + room.size() + " thành viên online)");
        return "ROOMS " + String.join("|", entries);
    }

    static final class Room {
        final String name;
        final List<ClientThread> members = new ArrayList<>();

        Room(String name) { this.name = name; }

        synchronized void add(ClientThread client) { members.add(client); }
        synchronized void remove(ClientThread client) { members.remove(client); }
        synchronized int size() { return members.size(); }

        synchronized String memberNames() {
            List<String> names = new ArrayList<>();
            for (ClientThread member : members) names.add(member.getUsername());
            return String.join(", ", names);
        }

        synchronized ClientThread findMember(String name) {
            for (ClientThread member : members) {
                if (member.getUsername().equalsIgnoreCase(name)) return member;
            }
            return null;
        }

        synchronized void broadcast(String message, ClientThread sender) {
            for (ClientThread member : members) {
                if (member != sender) member.send("MESSAGE " + message);
            }
        }
    }

}
