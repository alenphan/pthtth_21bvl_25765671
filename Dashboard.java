import java.io.IOException;
import java.util.Scanner;

public class Dashboard {
    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);
        System.out.println("===== CHAT CLIENT DASHBOARD =====");
        System.out.println("Backend/server phải được khởi động riêng trước.");
        System.out.print("Địa chỉ server [localhost]: ");
        String host = input.nextLine().trim();
        if (host.isEmpty()) host = "localhost";
        System.out.print("Cổng server [5000]: ");
        String port = input.nextLine().trim();
        if (port.isEmpty()) port = "5000";

        new ProcessBuilder(
                "java", "-cp", System.getProperty("java.class.path"), "Client", host, port)
                .inheritIO()
                .start();
    }
}
