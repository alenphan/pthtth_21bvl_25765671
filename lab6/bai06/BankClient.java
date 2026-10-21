package lab6.bai06;

import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class BankClient {
    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        BankService service = (BankService) LocateRegistry.getRegistry(host, 1106).lookup("BankService");
        try (Scanner in = new Scanner(System.in)) {
            while (true) {
                System.out.print("1 So du, 2 Gui, 3 Rut, 0 Thoat: ");
                int choice = Integer.parseInt(in.nextLine().trim());
                if (choice == 0) break;
                if (choice == 1) System.out.println("So du: " + service.getBalance());
                else if (choice == 2 || choice == 3) {
                    System.out.print("So tien: "); double amount = Double.parseDouble(in.nextLine().trim());
                    if (choice == 2) { service.deposit(amount); System.out.println("Gui tien thanh cong."); }
                    else System.out.println(service.withdraw(amount) ? "Rut tien thanh cong." : "Rut tien that bai.");
                } else System.out.println("Lua chon khong hop le.");
            }
        }
    }
}
