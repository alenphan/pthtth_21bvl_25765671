package bai08;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class FlightServer extends UnicastRemoteObject implements FlightService {
    private static final long serialVersionUID = 1L;
    private final Map<String, Integer> capacity = new HashMap<>();
    private final Map<String, Integer> available = new HashMap<>();
    private FlightServer() throws RemoteException {
        capacity.put("VN101", 100); capacity.put("VN202", 80);
        available.putAll(capacity);
    }
    public synchronized boolean bookTicket(String flight, int seats) {
        Integer count = available.get(flight);
        if (count == null || seats <= 0 || count < seats) return false;
        available.put(flight, count - seats); return true;
    }
    public synchronized int availableSeats(String flight) { return available.getOrDefault(flight, -1); }
    public synchronized boolean cancelBooking(String flight, int seats) {
        Integer count = available.get(flight);
        if (count == null || seats <= 0 || count + seats > capacity.get(flight)) return false;
        available.put(flight, count + seats); return true;
    }
    public static void main(String[] args) throws Exception {
        LocateRegistry.createRegistry(1108).rebind("FlightService", new FlightServer());
        System.out.println("Flight service ready on port 1108 (VN101, VN202).");
    }
}
