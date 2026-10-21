package lab6.bai09;

import java.rmi.registry.LocateRegistry;
import java.util.List;
import java.util.Scanner;

public class AuctionClient {
    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        AuctionService service = (AuctionService) LocateRegistry.getRegistry(host, 1109).lookup("AuctionService");
        try (Scanner in = new Scanner(System.in)) {
            while (true) {
                System.out.print("1 Dang san pham, 2 Xem san pham, 3 Dat gia, 4 Thong bao, 0 Thoat: ");
                int choice = Integer.parseInt(in.nextLine().trim());
                if (choice == 0) break;
                if (choice == 1) {
                    System.out.print("Ten nguoi ban: "); String seller = in.nextLine();
                    System.out.print("Ten san pham: "); String name = in.nextLine();
                    System.out.print("Gia khoi diem: "); double price = Double.parseDouble(in.nextLine().trim());
                    System.out.println("Ma san pham: " + service.addProduct(seller, name, price));
                } else if (choice == 2) {
                    List<String> products = service.listProducts();
                    if (products.isEmpty()) System.out.println("Chua co san pham.");
                    else products.forEach(System.out::println);
                } else if (choice == 3) {
                    System.out.print("Ma san pham: "); String id = in.nextLine().trim();
                    System.out.print("Ten nguoi dau gia: "); String bidder = in.nextLine();
                    System.out.print("Gia dat: "); double amount = Double.parseDouble(in.nextLine().trim());
                    System.out.println(service.bid(id, bidder, amount) ? "Dat gia thanh cong." : "Gia phai cao hon gia hien tai.");
                } else if (choice == 4) {
                    System.out.print("Ten tai khoan: "); String user = in.nextLine();
                    List<String> messages = service.getNotifications(user);
                    if (messages.isEmpty()) System.out.println("Khong co thong bao.");
                    else messages.forEach(System.out::println);
                } else System.out.println("Lua chon khong hop le.");
            }
        }
    }
}
