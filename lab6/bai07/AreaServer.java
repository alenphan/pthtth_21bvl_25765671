package bai07;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class AreaServer extends UnicastRemoteObject implements AreaService {
    private static final long serialVersionUID = 1L;
    private AreaServer() throws RemoteException { }
    private static void positive(double value) {
        if (!Double.isFinite(value) || value < 0) throw new IllegalArgumentException("Kich thuoc phai khong am.");
    }
    public double rectangleArea(double width, double height) { positive(width); positive(height); return width * height; }
    public double circleArea(double radius) { positive(radius); return Math.PI * radius * radius; }
    public double triangleArea(double base, double height) { positive(base); positive(height); return base * height / 2; }
    public static void main(String[] args) throws Exception {
        LocateRegistry.createRegistry(1107).rebind("AreaService", new AreaServer());
        System.out.println("Area service ready on port 1107.");
    }
}
