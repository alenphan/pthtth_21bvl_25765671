package bai09;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AuctionServer extends UnicastRemoteObject implements AuctionService {
    private static final long serialVersionUID = 1L;
    private static final class Product {
        final String id, seller, name;
        double price;
        String bidder = "Chua co";
        Product(String id, String seller, String name, double price) {
            this.id = id; this.seller = seller; this.name = name; this.price = price;
        }
    }
    private final Map<String, Product> products = new HashMap<>();
    private final Map<String, List<String>> notifications = new HashMap<>();
    private AuctionServer() throws RemoteException { }
    public synchronized String addProduct(String seller, String name, double price) {
        if (seller == null || seller.isBlank() || name == null || name.isBlank()
                || !Double.isFinite(price) || price < 0) throw new IllegalArgumentException("Thong tin san pham khong hop le.");
        String id = UUID.randomUUID().toString().substring(0, 8);
        products.put(id, new Product(id, seller, name, price));
        return id;
    }
    public synchronized List<String> listProducts() {
        List<String> result = new ArrayList<>();
        for (Product p : products.values())
            result.add(p.id + " | " + p.name + " | gia=" + p.price + " | nguoi dan=" + p.bidder + " | seller=" + p.seller);
        return result;
    }
    public synchronized boolean bid(String id, String bidder, double amount) {
        Product p = products.get(id);
        if (p == null || bidder == null || bidder.isBlank() || !Double.isFinite(amount) || amount <= p.price) return false;
        String previousBidder = p.bidder;
        p.price = amount; p.bidder = bidder;
        String message = "Gia moi cho '" + p.name + "' la " + amount + " do " + bidder + " dat.";
        for (String user : new String[] {p.seller, previousBidder})
            if (!user.equals("Chua co") && !user.equals(bidder))
                notifications.computeIfAbsent(user, ignored -> new ArrayList<>()).add(message);
        System.out.println(message);
        return true;
    }
    public synchronized List<String> getNotifications(String bidder) {
        List<String> result = new ArrayList<>(notifications.getOrDefault(bidder, List.of()));
        notifications.remove(bidder);
        return result;
    }
    public static void main(String[] args) throws Exception {
        LocateRegistry.createRegistry(1109).rebind("AuctionService", new AuctionServer());
        System.out.println("Auction service ready on port 1109.");
    }
}
