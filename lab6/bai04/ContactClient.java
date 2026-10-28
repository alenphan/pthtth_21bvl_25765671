package bai04;

import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class ContactClient {
    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        ContactService service = (ContactService) LocateRegistry.getRegistry(host, 1104).lookup("ContactService");
        try (Scanner in = new Scanner(System.in)) {
            while (true) {
                System.out.print("1 Them, 2 Tim, 3 Sua, 4 Xoa, 0 Thoat: ");
                int choice = Integer.parseInt(in.nextLine().trim());
                if (choice == 0) break;
                System.out.print("Ten: "); String name = in.nextLine();
                if (choice == 1 || choice == 3) {
                    System.out.print("So dien thoai: "); String phone = in.nextLine();
                    if (choice == 1) { service.addContact(name, phone); System.out.println("Da them."); }
                    else System.out.println(service.updateContact(name, phone) ? "Da cap nhat." : "Khong tim thay.");
                } else if (choice == 2) {
                    String phone = service.findContact(name);
                    System.out.println(phone == null ? "Khong tim thay." : name + ": " + phone);
                } else if (choice == 4) System.out.println(service.deleteContact(name) ? "Da xoa." : "Khong tim thay.");
                else System.out.println("Lua chon khong hop le.");
            }
        }
    }
}
