package lab4.exercises.ex08_filetransfer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class FileClient {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Cú pháp: java lab4.exercises.ex08_filetransfer.FileClient <đường_dẫn_file> [host] [port]");
            return;
        }

        String filePath = args[0];
        String host = args.length > 1 ? args[1] : "localhost";
        int port = args.length > 2 ? Integer.parseInt(args[2]) : 5010;

        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            System.err.println("Lỗi: File '" + filePath + "' không tồn tại!");
            return;
        }

        System.out.println(">>> Đang chuẩn bị truyền file: " + file.getName() + " (" + file.length() + " bytes)...");

        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                byte[] buf = new byte[8192];
                int read;
                while ((read = bis.read(buf)) != -1) {
                    digest.update(buf, 0, read);
                }
            }
            byte[] hashBytes = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            String sha256Hex = hexString.toString();
            System.out.println("Mã SHA-256 của file: " + sha256Hex);

            try (Socket socket = new Socket(host, port);
                 BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
                 PrintWriter textOut = new PrintWriter(new OutputStreamWriter(bos, StandardCharsets.UTF_8), true);
                 BufferedReader textIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

                textOut.println("UPLOAD " + file.getName() + " " + file.length() + " " + sha256Hex);

                String serverReady = textIn.readLine();
                if (!"OK READY_FOR_BYTES".equals(serverReady)) {
                    System.err.println("Server từ chối nhận file: " + serverReady);
                    return;
                }

                System.out.println("Đang truyền dữ liệu byte...");
                try (BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(file))) {
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = fileIn.read(buffer)) != -1) {
                        bos.write(buffer, 0, len);
                    }
                    bos.flush();
                }

                String result = textIn.readLine();
                System.out.println("Kết quả từ Server: " + result);
            }

        } catch (Exception e) {
            System.err.println("Lỗi truyền file: " + e.getMessage());
        }
    }
}

