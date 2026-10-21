package lab6.bai09;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface AuctionService extends Remote {
    String addProduct(String seller, String name, double startingPrice) throws RemoteException;
    List<String> listProducts() throws RemoteException;
    boolean bid(String productId, String bidder, double amount) throws RemoteException;
    List<String> getNotifications(String bidder) throws RemoteException;
}
