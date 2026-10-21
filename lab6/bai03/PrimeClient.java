package lab6.bai03;

import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class PrimeClient {
    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        PrimeService service = (PrimeService) LocateRegistry.getRegistry(host, 1103).lookup("PrimeService");
        try (Scanner input = new Scanner(System.in)) {
            System.out.print("Nhap so nguyen: "); int n = input.nextInt();
            System.out.println(n + (service.isPrime(n) ? " la so nguyen to." : " khong la so nguyen to."));
        }
    }
}
