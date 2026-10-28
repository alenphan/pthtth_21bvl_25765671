package bai06;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class BankServer extends UnicastRemoteObject implements BankService {
    private static final long serialVersionUID = 1L;
    private double balance = 1000.0;
    private BankServer() throws RemoteException { }
    public synchronized double getBalance() { return balance; }
    public synchronized void deposit(double amount) {
        if (!Double.isFinite(amount) || amount <= 0) throw new IllegalArgumentException("So tien gui phai lon hon 0.");
        balance += amount;
    }
    public synchronized boolean withdraw(double amount) {
        if (!Double.isFinite(amount) || amount <= 0) return false;
        if (amount > balance) return false;
        balance -= amount; return true;
    }
    public static void main(String[] args) throws Exception {
        LocateRegistry.createRegistry(1106).rebind("BankService", new BankServer());
        System.out.println("Bank service ready on port 1106; initial balance = 1000.0.");
    }
}
