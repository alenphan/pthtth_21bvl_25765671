package lab4.network;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostInspector {
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Cú pháp: java lab4.network.HostInspector <hostname>");
            System.out.println("Ví dụ : java lab4.network.HostInspector localhost");
            System.out.println("        java lab4.network.HostInspector google.com");
            return;
        }

        String host = args[0];
        try {

            InetAddress[] addresses = InetAddress.getAllByName(host);
            System.out.println("==========================================");
            System.out.println("Kết quả tra cứu Host: " + host);
            System.out.println("Số lượng địa chỉ tìm thấy: " + addresses.length);
            System.out.println("------------------------------------------");

            for (InetAddress address : addresses) {
                System.out.println("IP Address    : " + address.getHostAddress());

                if (address instanceof Inet4Address) {
                    System.out.println("Loại IP       : IPv4 (32-bit)");
                } else if (address instanceof Inet6Address) {
                    System.out.println("Loại IP       : IPv6 (128-bit)");
                } else {
                    System.out.println("Loại IP       : Không xác định");
                }

                System.out.println("Canonical Host: " + address.getCanonicalHostName());
                System.out.println("Loopback?     : " + (address.isLoopbackAddress() ? "Đúng (Máy cục bộ)" : "Không"));
                System.out.println("Site local?   : " + (address.isSiteLocalAddress() ? "Đúng (Mạng riêng LAN)" : "Không (Public Internet)"));
                System.out.println("------------------------------------------");
            }
        } catch (UnknownHostException e) {

            System.err.println("Lỗi: Không thể phân giải được hostname '" + host + "'. Vui lòng kiểm tra lại kết nối mạng hoặc tên miền!");
        }
    }
}

