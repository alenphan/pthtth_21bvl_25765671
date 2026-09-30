package lab4.network;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class UriInspector {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Cú pháp: java lab4.network.UriInspector <hostname> <uri>");
            System.out.println("Ví dụ  : java lab4.network.UriInspector localhost https://fit.iuh.edu.vn:8443/courses?dept=cntt#syllabus");
            return;
        }

        String host = args[0];
        String uriString = args[1];

        System.out.println("==================================================");
        System.out.println("PHẦN 1: KHẢO SÁT HOSTNAME -> " + host);
        System.out.println("==================================================");
        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);
            for (InetAddress addr : addresses) {
                String ipType = (addr instanceof Inet4Address) ? "IPv4" :
                                (addr instanceof Inet6Address) ? "IPv6" : "Khác";
                System.out.printf("- IP: %-25s | Loại: %-4s | Loopback: %-5s | SiteLocal: %-5s%n",
                        addr.getHostAddress(), ipType, addr.isLoopbackAddress(), addr.isSiteLocalAddress());
            }
        } catch (UnknownHostException e) {
            System.err.println("Lỗi: Không tìm thấy host: " + host);
        }

        System.out.println("\n==================================================");
        System.out.println("PHẦN 2: PHÂN TÍCH THÀNH PHẦN URI -> " + uriString);
        System.out.println("==================================================");
        try {
            URI uri = new URI(uriString);
            System.out.println("Scheme (Giao thức) : " + (uri.getScheme() != null ? uri.getScheme() : "(none)"));
            System.out.println("User Info          : " + (uri.getUserInfo() != null ? uri.getUserInfo() : "(none)"));
            System.out.println("Host               : " + (uri.getHost() != null ? uri.getHost() : "(none)"));
            System.out.println("Port               : " + (uri.getPort() != -1 ? uri.getPort() : "Mặc định"));
            System.out.println("Path (Đường dẫn)   : " + (uri.getPath() != null ? uri.getPath() : "(none)"));
            System.out.println("Query (Tham số)    : " + (uri.getQuery() != null ? uri.getQuery() : "(none)"));
            System.out.println("Fragment (Neo tag) : " + (uri.getFragment() != null ? uri.getFragment() : "(none)"));
        } catch (URISyntaxException e) {
            System.err.println("Lỗi: URI không hợp lệ! Chi tiết: " + e.getMessage());
        }
    }
}

