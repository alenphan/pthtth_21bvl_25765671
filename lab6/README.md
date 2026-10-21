# Lab 6 – Java RMI

## Biên dịch

Chạy từ thư mục gốc của dự án:

```sh
mkdir -p out
javac -d out lab6/*.java lab6/bai02/*.java lab6/bai03/*.java \
  lab6/bai04/*.java lab6/bai05/*.java lab6/bai06/*.java \
  lab6/bai07/*.java lab6/bai08/*.java lab6/bai09/*.java
```

## Chạy từng bài

Mở hai terminal. Khởi động server trước, sau đó chạy client tương ứng. Thay `localhost` bằng địa chỉ máy chủ nếu client chạy trên máy khác.

| Bài | Server | Client | Cổng |
|---|---|---|---:|
| 1 – Hello | `java -cp out HelloServer` | `java -cp out HelloClient [host]` | 1099 |
| 2 – Cộng hai số | `java -cp out lab6.bai02.AddServer` | `java -cp out lab6.bai02.AddClient [host]` | 1102 |
| 3 – Số nguyên tố | `java -cp out lab6.bai03.PrimeServer` | `java -cp out lab6.bai03.PrimeClient [host]` | 1103 |
| 4 – Danh bạ | `java -cp out lab6.bai04.ContactServer` | `java -cp out lab6.bai04.ContactClient [host]` | 1104 |
| 5 – Chat | `java -cp out lab6.bai05.ChatServer` | `java -cp out lab6.bai05.ChatClient [host]` | 1105 |
| 6 – Ngân hàng | `java -cp out lab6.bai06.BankServer` | `java -cp out lab6.bai06.BankClient [host]` | 1106 |
| 7 – Diện tích | `java -cp out lab6.bai07.AreaServer` | `java -cp out lab6.bai07.AreaClient [host]` | 1107 |
| 8 – Đặt vé | `java -cp out lab6.bai08.FlightServer` | `java -cp out lab6.bai08.FlightClient [host]` | 1108 |
| 9 – Đấu giá | `java -cp out lab6.bai09.AuctionServer` | `java -cp out lab6.bai09.AuctionClient [host]` | 1109 |

Các server tự tạo RMI registry trên cổng riêng. Bài 8 có sẵn chuyến bay `VN101` (100 ghế) và `VN202` (80 ghế). Bài 9 gửi thông báo giá mới cho người bán và người đã đấu giá trước đó; người dùng xem thông báo trong menu client.
