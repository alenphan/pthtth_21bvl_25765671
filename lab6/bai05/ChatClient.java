package bai05;

import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        ChatService service = (ChatService) LocateRegistry.getRegistry(host, 1105).lookup("ChatService");
        try (Scanner in = new Scanner(System.in)) {
            System.out.println("Chat bat dau; nhap /exit de thoat.");
            while (true) {
                System.out.print("Ban: "); String message = in.nextLine();
                if (message.equalsIgnoreCase("/exit")) break;
                service.sendMessage(message);
                System.out.println(service.getResponse());
            }
        }
    }
}
