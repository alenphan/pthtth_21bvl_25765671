package lab6.bai03;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PrimeService extends Remote {
    boolean isPrime(int n) throws RemoteException;
}
