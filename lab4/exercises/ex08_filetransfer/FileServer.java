package lab4.exercises.ex08_filetransfer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class FileServer {
    public static final int PORT = 5010;
    private static final String UPLOAD_DIR = "lab4/uploads";

    public static void main(String[] args) {
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        System.out.println(">>> Đang khởi động Secure File Server trên cổng " + PORT + "...");
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server sẵn sàng nhận file tại: " + dir.getAbsolutePath());

            while (true) {
                Socket socket = server.accept();
                new Thread(() -> handleUpload(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("Lỗi Server: " + e.getMessage());
        }
    }

    private static void handleUpload(Socket socket) {
        try (socket;
             BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(bis, StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)) {

            String header = reader.readLine();
            if (header == null || !header.startsWith("UPLOAD ")) {
                out.println("ERR INVALID_HEADER");
                return;
            }

            String[] parts = header.split("\\s+");
            if (parts.length != 4) {
                out.println("ERR INVALID_METADATA");
                return;
            }

            String rawFileName = parts[1];
            long fileSize = Long.parseLong(parts[2]);
            String expectedSha256 = parts[3];

            String safeFileName = new File(rawFileName).getName();
            File destFile = new File(UPLOAD_DIR, safeFileName);
            System.out.println("[Nhận File]: " + safeFileName + " (" + fileSize + " bytes)");

            out.println("OK READY_FOR_BYTES");

            MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
            try (BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(destFile))) {
                byte[] buffer = new byte[8192];
                long remaining = fileSize;
                while (remaining > 0) {
                    int toRead = (int) Math.min(buffer.length, remaining);
                    int bytesRead = bis.read(buffer, 0, toRead);
                    if (bytesRead == -1) {
                        break;
                    }
                    fos.write(buffer, 0, bytesRead);
                    sha256Digest.update(buffer, 0, bytesRead);
                    remaining -= bytesRead;
                }
                fos.flush();
            }

            byte[] hashBytes = sha256Digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            String actualSha256 = hexString.toString();

            if (actualSha256.equalsIgnoreCase(expectedSha256)) {
                System.out.println("[+] File " + safeFileName + " đã nhận thành công, SHA-256 chính xác!");
                out.println("OK UPLOAD_SUCCESS " + actualSha256);
            } else {
                System.err.println("[-] Lỗi sai lệch SHA-256 cho file: " + safeFileName);
                out.println("ERR HASH_MISMATCH");
            }

        } catch (Exception e) {
            System.err.println("Lỗi tiếp nhận file: " + e.getMessage());
        }
    }
}

