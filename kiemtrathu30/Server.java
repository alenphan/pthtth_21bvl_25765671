import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(7300)) {
            System.out.println("Server listening on port 7300...");
            while (true) {
                Socket client = server.accept();
                new ServerThread(client).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ServerThread extends Thread {
    private final Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Gui danh sach dich vu
            out.println("=== DANH SACH DICH VU ===");
            out.println("1. Dao nguoc toan bo chuoi va in hoa ki tu dau tung tu");
            out.println("2. Dao nguoc tu va in hoa ki tu dau tung tu, giu nguyen vi tri");
            out.println("3. Dem so luong tu cua tat ca cac dong");
            out.println("4. Dem so luong tu tung dong");
            out.println("Chon dich vu: ");

            int chosen = Integer.parseInt(in.readLine().trim());

            // Nhan cac dong chuoi den khi nhan dau "."
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = in.readLine()) != null) {
                if (line.trim().equals(".")) break;
                lines.add(line);
            }

            switch (chosen) {
                case 1:
                    for (String s : lines) out.println(dichVu1(s));
                    break;
                case 2:
                    for (String s : lines) out.println(dichVu2(s));
                    break;
                case 3:
                    int total = 0;
                    for (String s : lines) total += demTu(s);
                    out.println("Tong so luong tu: " + total);
                    break;
                case 4:
                    for (String s : lines) out.println("So tu: " + demTu(s));
                    break;
                default:
                    out.println("Dich vu khong hop le!");
            }

            // Bao hieu ket thuc ket qua va dong ket noi
            out.println("EXIT.");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Dich vu 1: dao nguoc toan bo chuoi, in hoa ki tu dau tung tu
    private String dichVu1(String s) {
        StringBuilder sb = new StringBuilder(s).reverse();
        return inHoaDau(sb.toString());
    }

    // Dich vu 2: dao nguoc ky tu tung tu, giu nguyen vi tri, in hoa dau tung tu
    private String dichVu2(String s) {
        String[] words = s.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            sb.append(new StringBuilder(w).reverse()).append(" ");
        }
        return inHoaDau(sb.toString().trim());
    }

    // In hoa ki tu dau cua tung tu
    private String inHoaDau(String s) {
        String[] words = s.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0)))
                  .append(w.substring(1).toLowerCase()).append(" ");
            }
        }
        return sb.toString().trim();
    }

    // Dem so luong tu trong 1 dong
    private int demTu(String s) {
        String trimmed = s.trim();
        if (trimmed.isEmpty()) return 0;
        return trimmed.split("\\s+").length;
    }
}
