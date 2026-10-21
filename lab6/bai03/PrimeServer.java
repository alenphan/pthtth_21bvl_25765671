package lab6.bai03;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class PrimeServer extends UnicastRemoteObject implements PrimeService {
    private static final long serialVersionUID = 1L;
    private PrimeServer() throws RemoteException { }
    public boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i <= n / i; i++) if (n % i == 0) return false;
        return true;
    }
    public static void main(String[] args) throws Exception {
        LocateRegistry.createRegistry(1103).rebind("PrimeService", new PrimeServer());
        System.out.println("Prime service ready on port 1103.");
    }
}
