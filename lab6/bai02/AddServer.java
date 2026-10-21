package lab6.bai02;

import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class AddServer extends UnicastRemoteObject implements AddService {
    private static final long serialVersionUID = 1L;
    private AddServer() throws java.rmi.RemoteException { }
    public int add(int a, int b) { return a + b; }

    public static void main(String[] args) throws Exception {
        LocateRegistry.createRegistry(1102).rebind("AddService", new AddServer());
        System.out.println("Add service ready on port 1102.");
    }
}
