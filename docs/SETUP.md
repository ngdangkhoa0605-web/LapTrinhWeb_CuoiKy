# Hướng dẫn cài đặt và kiểm tra

## Yêu cầu
JDK 17+, Maven 3.8+, MySQL 8+ (hoặc SQL Server/PostgreSQL), trình duyệt hiện đại. Xem `README.md` để biết thêm luồng nghiệp vụ.

## Thiết lập
1. Tạo database MySQL `bookstore` (hoặc cấp quyền tạo database cho tài khoản dùng trong JDBC URL).
2. Thiết lập biến môi trường **trong terminal sẽ chạy Maven**, ví dụ PowerShell:
   ```powershell
   $env:DB_USERNAME='root'
   $env:DB_PASSWORD='mat-khau-cua-ban'
   $env:MAIL_USERNAME='email-gmail-cua-ban'
   $env:MAIL_PASSWORD='app-password-cua-ban'
   $env:CLOUDINARY_CLOUD_NAME='cloud-name-cua-ban'
   $env:CLOUDINARY_API_KEY='api-key-cua-ban'
   $env:CLOUDINARY_API_SECRET='api-secret-cua-ban'
   $env:JWT_SECRET='mot-chuoi-ngau-nhien-toi-thieu-32-ky-tu'
   ```
3. Chọn profile `mysql` (mặc định), `sqlserver` hoặc `postgresql` qua `SPRING_PROFILES_ACTIVE`. Đổi URL kết nối ở `src/main/resources/application-{profile}.yml` nếu máy dùng cổng/host khác.
4. Chạy `mvn clean test`, sau đó `mvn spring-boot:run` và vào `http://localhost:8080/home`.
5. Đăng nhập admin khởi tạo (`admin` / `admin@123`), đổi mật khẩu trước khi triển khai công khai; thêm tài khoản manager/staff tại `/admin/users`.

## Dữ liệu mẫu và khởi động nhanh trên máy này
Chạy `& .\docs\start-local.ps1` trong PowerShell ở thư mục dự án **trên máy đã chuẩn bị Maven/MariaDB tại đường dẫn trong script** để bật MariaDB và website local. Script chỉ bật `DEMO_SEED_BOOKS=true` cho lần chạy local; dữ liệu chỉ được thêm khi bảng `books` rỗng. Seed gốc có 12 sách và 4 ảnh SVG nội bộ; bản xuất `database/bookstore-demo.sql` mới có **111 sách / 5 danh mục / 3 chi nhánh** sau khi thêm metadata Open Library. Có thể nhập bản xuất vào database trống để khôi phục toàn bộ dữ liệu. Ảnh bìa các sách Open Library cần Internet. Không dùng seed và SQL demo cho môi trường production.

## Chạy Spring Boot độc lập (không phụ thuộc VS Code)
Trên máy Windows đã cài JDK 21, Maven và MariaDB tại đường dẫn mô tả ở trên, đóng gói một lần bằng `mvn clean package` (hoặc Maven local tại `Downloads/bookstore-local-tools/apache-maven-3.9.9/bin/mvn.cmd`). Chạy `& .\docs\start-springboot-war.ps1` từ thư mục dự án: script bật MariaDB rồi chạy `target/bookstore-chain-management.war` bằng `java -jar` trong tiến trình Windows riêng. Dùng `& .\docs\start-springboot-war.ps1 -Restart` để khởi động lại. Truy cập `http://localhost:8080/home`. Xem log trong `Downloads/bookstore-local-tools/war.log` nếu có lỗi. Không cần mở VS Code.

**Cloudinary upload:** Cần đủ ba giá trị từ đúng một tài khoản Cloudinary: `cloud name`, `API key`, `API secret`. Mở PowerShell ngoài VS Code, chạy `& .\docs\configure-cloudinary-local.ps1`; nhập giá trị theo lời nhắc (key/secret không hiện lên màn hình). Script lưu mã hóa theo tài khoản Windows tại `%APPDATA%/BookstoreChainLocal`, không lưu vào source/ZIP. Sau đó chạy `& .\docs\start-springboot-war.ps1 -Restart`; đăng nhập manager/staff được gán chi nhánh và thử thêm ảnh sách để xác minh upload. Nếu chưa có cloud name hoặc không có manager/staff, chưa thể kiểm thử upload thực tế. API key/secret đã từng được chia sẻ qua hội thoại nên cần thay vòng trên Cloudinary trước khi dùng thật.

## Kiểm thử thủ công
* Guest: trang chủ, tìm sách, lọc danh mục/chi nhánh, phân trang, chi tiết; kiểm tra sách chưa có ảnh không hiển thị placeholder lỗi.
* Customer: đăng ký → OTP email → đăng nhập → thêm giỏ → checkout → đơn hàng của tôi.
* Manager/Staff: thêm/sửa sách, đơn hàng chi nhánh, cập nhật trạng thái, dashboard.
* Admin: quản lý chi nhánh, danh mục, tài khoản, đơn hàng và dashboard.
* REST: đăng nhập lấy JWT rồi thử API `/api/books`; theo cấu hình SecurityConfig để xác thực.

## Lưu ý an toàn
Các khóa SMTP, Cloudinary và mật khẩu database từng bị nhúng trực tiếp trong source trước khi được chuyển sang biến môi trường: **thu hồi/đổi các khóa cũ trên dịch vụ thật**. Không đẩy file dump chứa dữ liệu cá nhân lên kho công khai. Các tính năng OTP/upload ảnh cần cấu hình dịch vụ thật để chạy.