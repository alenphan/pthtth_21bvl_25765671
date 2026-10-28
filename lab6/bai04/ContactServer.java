package bai04;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class ContactServer extends UnicastRemoteObject implements ContactService {
    private static final long serialVersionUID = 1L;
    private final Map<String, String> contacts = new HashMap<>();
    private ContactServer() throws RemoteException { }
    public synchronized void addContact(String name, String phone) { contacts.put(name, phone); }
    public synchronized String findContact(String name) { return contacts.get(name); }
    public synchronized boolean deleteContact(String name) { return contacts.remove(name) != null; }
    public synchronized boolean updateContact(String name, String phone) {
        if (!contacts.containsKey(name)) return false;
        contacts.put(name, phone); return true;
    }
    public static void main(String[] args) throws Exception {
        LocateRegistry.createRegistry(1104).rebind("ContactService", new ContactServer());
        System.out.println("Contact service ready on port 1104.");
    }
}
