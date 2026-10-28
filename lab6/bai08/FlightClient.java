package bai08;

import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class FlightClient {
    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        FlightService service = (FlightService) LocateRegistry.getRegistry(host, 1108).lookup("FlightService");
        try (Scanner in = new Scanner(System.in)) {
            while (true) {
                System.out.print("1 Dat, 2 Ghe trong, 3 Huy, 0 Thoat: ");
                int choice = Integer.parseInt(in.nextLine().trim());
                if (choice == 0) break;
                System.out.print("Ma chuyen bay: "); String flight = in.nextLine().trim();
                if (choice == 2) {
                    int seats = service.availableSeats(flight);
                    System.out.println(seats < 0 ? "Khong tim thay chuyen bay." : "So ghe trong: " + seats);
                } else if (choice == 1 || choice == 3) {
                    System.out.print("So ghe: "); int seats = Integer.parseInt(in.nextLine().trim());
                    boolean ok = choice == 1 ? service.bookTicket(flight, seats) : service.cancelBooking(flight, seats);
                    System.out.println(ok ? "Yeu cau thanh cong." : "Yeu cau that bai.");
                } else System.out.println("Lua chon khong hop le.");
            }
        }
    }
}
