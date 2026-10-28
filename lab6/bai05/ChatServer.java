package bai05;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalTime;

public class ChatServer extends UnicastRemoteObject implements ChatService {
    private static final long serialVersionUID = 1L;
    private volatile String response = "Xin chao! Server da san sang tro chuyen.";
    private ChatServer() throws RemoteException { }
    public void sendMessage(String message) {
        System.out.println("Client: " + message);
        response = "Server da nhan: " + message + " (" + LocalTime.now().withNano(0) + ")";
    }
    public String getResponse() { return response; }
    public static void main(String[] args) throws Exception {
        LocateRegistry.createRegistry(1105).rebind("ChatService", new ChatServer());
        System.out.println("Chat service ready on port 1105.");
    }
}
