# Chạy trên máy khác với MySQL

## 1. Chuẩn bị

Cài Git, JDK 17 hoặc 21, Maven 3.8+ và MySQL Community 8.4. Khởi động MySQL. Clone repo `https://github.com/ngdangkhoa0605-web/LapTrinhWeb_CuoiKy.git`, rồi mở PowerShell tại thư mục vừa clone (thư mục có `pom.xml`). Không cần VS Code.

## 2. Nhập database

File `database/bookstore-mysql.sql` gồm cấu trúc 10 bảng và dữ liệu 111 sách, 5 danh mục, 3 chi nhánh, 4 vai trò. Có sẵn mô tả bổ sung từ Open Library. Không chứa dữ liệu người dùng, đơn hàng hoặc khóa Cloudinary.

**Chỉ nhập vào database trống:** file dump có `DROP TABLE`, không nhập lên database đang có dữ liệu cần giữ. Không cần nhập thêm `openlibrary-100.sql` hay `openlibrary-descriptions.sql`.

Mở MySQL Command Line Client hoặc MySQL Workbench, kết nối bằng tài khoản có quyền tạo database. Chạy:

```sql
CREATE DATABASE bookstore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Trong MySQL Workbench: Server → Data Import → Import from Self-Contained File → chọn file `database/bookstore-mysql.sql` trong repo vừa clone → Default Target Schema: `bookstore` → Start Import.

Hoặc trong MySQL Command Line Client: chọn `USE bookstore;`, sau đó dùng lệnh `SOURCE` với **đường dẫn tuyệt đối** đến file SQL vừa tải (dùng dấu `/` thay cho `\`).

Kiểm tra:

```sql
SELECT COUNT(*) FROM bookstore.books; -- 111
SELECT COUNT(*) FROM bookstore.categories; -- 5
SELECT COUNT(*) FROM bookstore.stores; -- 3
```

## 3. Cấu hình và chạy Spring Boot

Trong PowerShell tại thư mục repo:

```powershell
$env:SPRING_PROFILES_ACTIVE = 'mysql'
$env:DB_URL = 'jdbc:mysql://127.0.0.1:3306/bookstore?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8'
$env:DB_USERNAME = Read-Host 'MySQL username'
$credential = Get-Credential -UserName $env:DB_USERNAME -Message 'Nhap mat khau MySQL'
$env:DB_PASSWORD = $credential.GetNetworkCredential().Password
mvn clean package
if ($LASTEXITCODE -ne 0) { throw 'Build failed' }
$war = Join-Path (Get-Location) 'target/bookstore-chain-management.war'
java -jar $war
```

Đổi port `3306` nếu MySQL trên máy bạn dùng port khác. Truy cập http://localhost:8080/. Giữ cửa sổ Java đang chạy; Ctrl+C để dừng. Không dùng `start-springboot.cmd` cho máy mới vì script đó gắn với bản MySQL portable và cấu hình mã hóa của máy phát triển cũ.

Ứng dụng tự tạo tài khoản demo `admin` / `admin@123` ở lần chạy đầu. Đổi mật khẩu trước khi đưa lên Internet. Các tài khoản/đơn hàng của máy cũ không được xuất để bảo vệ dữ liệu riêng.

## 4. Cloudinary và email (tùy chọn)

Xem sách và ảnh Open Library không cần khóa Cloudinary. Để upload ảnh, đặt đủ `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET` trong cùng PowerShell **trước khi chạy Java**. Nhập secret bằng `Read-Host -AsSecureString` và chuyển thành chuỗi chỉ trong bộ nhớ nếu cần. Không commit khóa vào Git. File khóa mã hóa của Windows trên máy cũ không dùng được trên máy mới.

Gửi OTP cần `MAIL_USERNAME`, `MAIL_PASSWORD`. Giá/tồn kho là dữ liệu demo; mô tả là metadata/giới thiệu, không phải toàn văn sách. Ảnh Open Library cần Internet.