import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class HelloServer {
    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind("HelloService", new HelloServiceImpl());
        System.out.println("Hello RMI server is ready on port 1099.");
    }
}
