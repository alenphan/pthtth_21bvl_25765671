package bai07;

import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class AreaClient {
    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        AreaService service = (AreaService) LocateRegistry.getRegistry(host, 1107).lookup("AreaService");
        try (Scanner in = new Scanner(System.in)) {
            System.out.print("1 Chu nhat, 2 Tron, 3 Tam giac: "); int choice = in.nextInt();
            if (choice == 1) {
                System.out.print("Chieu rong va chieu cao: ");
                System.out.println("Dien tich = " + service.rectangleArea(in.nextDouble(), in.nextDouble()));
            } else if (choice == 2) {
                System.out.print("Ban kinh: "); System.out.println("Dien tich = " + service.circleArea(in.nextDouble()));
            } else if (choice == 3) {
                System.out.print("Day va chieu cao: ");
                System.out.println("Dien tich = " + service.triangleArea(in.nextDouble(), in.nextDouble()));
            } else System.out.println("Lua chon khong hop le.");
        }
    }
}
