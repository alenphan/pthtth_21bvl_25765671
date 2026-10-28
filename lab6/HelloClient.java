import java.rmi.registry.LocateRegistry;

public class HelloClient {
    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        HelloService service = (HelloService) LocateRegistry.getRegistry(host, 1099)
                .lookup("HelloService");
        System.out.println(service.sayHello());
    }
}
