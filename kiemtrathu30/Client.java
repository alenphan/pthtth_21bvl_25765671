import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public static void main(String[] args) {
        String serverIp = "localhost";
        int port = 7300;

        try (Socket socket = new Socket(serverIp, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner sc = new Scanner(System.in)) {

            // Nhan danh sach dich vu tu server
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                if (line.startsWith("Chon dich vu")) {
                    break;
                }
            }

            // Chon dich vu
            System.out.print("Nhap so dich vu: ");
            out.println(sc.nextLine());

            // Nhap va gui cac dong chuoi, ket thuc bang dau "."
            System.out.println("Nhap cac dong chuoi (nhap \".\" de ket thuc):");
            while (true) {
                String s = sc.nextLine();
                out.println(s);
                if (s.equals(".")) break;
            }

            // Nhan ket qua tu server
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                if (line.equals("EXIT.")) break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
