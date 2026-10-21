package lab6.bai02;

import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class AddClient {
    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        AddService service = (AddService) LocateRegistry.getRegistry(host, 1102).lookup("AddService");
        try (Scanner input = new Scanner(System.in)) {
            System.out.print("Nhap a: "); int a = input.nextInt();
            System.out.print("Nhap b: "); int b = input.nextInt();
            System.out.println("Tong = " + service.add(a, b));
        }
    }
}
