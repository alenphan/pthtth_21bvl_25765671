package lab4.exercises.ex09_multicast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class MulticastSender {
    public static void main(String[] args) {
        String message = args.length > 0 ? args[0] : "THÔNG BÁO KHẨN TỪ PHÒNG ĐÀO TẠO IUH!";
        System.out.println(">>> Đang chuẩn bị phát bản tin Multicast: " + message);

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress group = InetAddress.getByName(MulticastReceiver.GROUP_IP);
            byte[] data = message.getBytes(StandardCharsets.UTF_8);

            DatagramPacket packet = new DatagramPacket(data, data.length, group, MulticastReceiver.PORT);
            socket.send(packet);
            System.out.println("Đã phát bản tin thành công tới địa chỉ nhóm " + MulticastReceiver.GROUP_IP + ":" + MulticastReceiver.PORT);
        } catch (Exception e) {
            System.err.println("Lỗi gửi Multicast: " + e.getMessage());
        }
    }
}

