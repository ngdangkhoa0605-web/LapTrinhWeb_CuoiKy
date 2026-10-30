# Bookstore Chain Management System

**Chạy trên máy khác với database MySQL 111 sách:** xem [hướng dẫn nhập SQL và chạy Spring Boot](database/RUN-ON-ANOTHER-PC.md). File bàn giao: `database/bookstore-mysql.sql` (không chứa tài khoản người dùng hoặc khóa dịch vụ).

**Đề án cuối kỳ - Lập trình Web**
Website quản lý chuỗi cửa hàng sách cũ - **Spring Boot + JSP/JSTL + Bootstrap
5 + Spring Data JPA + MySQL/SQL Server/PostgreSQL + JWT + WebSocket +
Cloudinary**

---

## 1. Kiến trúc & Công nghệ

| Yêu cầu đề án      | Cách triển khai trong project |
|---------------------|--------------------------------|
| Spring Boot          | `spring-boot-starter-parent 3.3.4`, đóng gói WAR, chạy được cả bằng `mvn spring-boot:run` lẫn deploy lên Tomcat ngoài |
| JSP/JSTL              | Toàn bộ giao diện dùng JSP đặt tại `src/main/webapp/WEB-INF/views/`, ViewResolver cấu hình trong `WebMvcConfig.java` |
| Bootstrap             | Bootstrap 5.3 + Bootstrap Icons (CDN), responsive toàn bộ trang |
| JPA                   | Spring Data JPA + Hibernate, 10 entity, quan hệ đầy đủ 1-n/n-1 |
| MySQL/SQLServer/PostgreSQL | 3 file `application-{profile}.yml`, đổi qua `spring.profiles.active` (mặc định **MySQL**) |
| JWT                    | Dùng cho toàn bộ `/api/**` (REST API, stateless). Xem `SecurityConfig`, `JwtUtil`, `JwtAuthFilter` |
| WebSocket              | STOMP over SockJS (`/ws`), đẩy thông báo đơn hàng mới REAL-TIME tới dashboard Quản lý chi nhánh |
| Cloudinary             | Toàn bộ ảnh danh mục/sách/chi nhánh upload lên Cloudinary qua `CloudinaryService` |

### Quyết định kiến trúc quan trọng (đã thống nhất trước khi code)

1. **CSDL mặc định: MySQL** (code JPA/migration chuẩn theo MySQL, có sẵn 2
   profile còn lại cho SQL Server/PostgreSQL).
2. **Multi-store**: hệ thống quản lý **nhiều chi nhánh** (`Store`), mỗi
   chi nhánh có kho sách và nhân viên/quản lý riêng.
3. **JWT chỉ dùng cho REST API** (`/api/**`, stateless). Toàn bộ website
   JSP (khách hàng, quản trị, quản lý chi nhánh) dùng **Session** của
   Spring Security (form login truyền thống) - ổn định, phù hợp JSP.

---

## 2. Mô hình dữ liệu (10 Entity)

```
Role (1) ────< User >──── (1) Store
                │
                ├──< Cart (1-1) >──< CartItem >──── (n) Book
                │                                       │
                └──< Order >──< OrderDetail >───────────┘
                       │
                       └──── (n) Store

Category (1) ────< Book >──── (n) Store
Store (1) ────< Notification (real-time qua WebSocket)
```

- **User**: `roleId` (ADMIN/MANAGER/STAFF/CUSTOMER) + `storeId` (null với
  ADMIN/CUSTOMER) + `status/otp/otpExpire` (kích hoạt & quên mật khẩu).
- **Book**: thuộc 1 `Category` + 1 `Store`, có `conditionGrade` (tình
  trạng sách cũ: NEW/LIKE_NEW/GOOD/FAIR).
- **Order**: luôn thuộc **đúng 1 Store**. Nếu giỏ hàng có sách của nhiều
  chi nhánh khác nhau, `OrderService.checkout()` sẽ **tự tách thành nhiều
  Order riêng biệt** (mỗi Order 1 Store) - xem code có chú thích rõ.

---

## 3. Cấu trúc thư mục

```
src/main/java/vn/bookstore/
├── entity/            10 entity JPA
├── repository/         10 Spring Data JPA repository
├── security/            UserPrincipal, JwtUtil, JwtAuthFilter,
│                        UserDetailsServiceImpl, Custom*Handler
├── config/               SecurityConfig, WebMvcConfig (JSP ViewResolver),
│                        WebSocketConfig, CloudinaryConfig, DataSeeder
├── service/ + service/impl/   AuthService, UserService, StoreService,
│                        CategoryService, BookService, CartService,
│                        OrderService, NotificationService,
│                        CloudinaryService, MailService
├── controller/web/        Public: Home, Book, Auth (login/register/OTP),
│                        Cart, Checkout, MyOrders (Session-based)
├── controller/admin/       Admin: Dashboard, Store, Category, User, Order
├── controller/manager/      Manager/Staff: Dashboard (WebSocket), Book,
│                        Order (chỉ thao tác trên chi nhánh của mình)
├── controller/api/          REST API (JWT): AuthApiController,
│                        BookApiController
├── dto/                    RegisterRequest, LoginRequest, JwtResponse,
│                        ApiResponse, CheckoutRequest, NotificationPayload
└── exception/               BusinessException + 2 GlobalExceptionHandler
                           (1 cho "/api/**" trả JSON, 1 cho JSP trả view lỗi)

src/main/resources/
├── application.yml              Cấu hình chung (JWT, OTP, Mail, Cloudinary)
├── application-mysql.yml         Profile MySQL (mặc định)
├── application-sqlserver.yml      Profile SQL Server
├── application-postgresql.yml      Profile PostgreSQL
└── static/css, static/js           CSS/JS dùng chung

src/main/webapp/WEB-INF/views/
├── common/     header.jsp, footer.jsp, error.jsp
├── auth/       login, register, verify-otp, forgot-password, reset-password
├── store/      home, book-list, book-detail, cart, checkout, my-orders, order-detail
├── admin/      dashboard, store-list/form, category-list/form, user-list/form, order-list
└── manager/    dashboard (WebSocket), book-list/form, order-list/detail
```

---

## 4. Cài đặt & chạy project

### 4.1. Yêu cầu

- JDK 17+
- Maven 3.8+ (hoặc dùng Maven Wrapper nếu có)
- MySQL 8+ (mặc định) — hoặc SQL Server / PostgreSQL nếu muốn đổi profile
- Tài khoản Gmail (để gửi OTP) + **App Password**
- Tài khoản Cloudinary (miễn phí tại https://cloudinary.com)

### 4.2. Tạo Database

Với MySQL (mặc định), **không cần tạo DB thủ công** — connection string
trong `application-mysql.yml` đã có `createDatabaseIfNotExist=true`, và
`hibernate.hbm2ddl.auto=update` sẽ tự tạo toàn bộ 10 bảng theo đúng Entity
khi chạy lần đầu.

Với SQL Server / PostgreSQL: tạo trước 1 database rỗng tên
`bookstore_chain_db` (SQL Server/PostgreSQL không hỗ trợ tự tạo DB qua
connection string như MySQL), Hibernate sẽ tự tạo bảng bên trong.

### 4.3. Cấu hình

**a) Chọn CSDL** — sửa `application.yml`:
```yaml
spring:
  profiles:
    active: mysql   # hoặc: sqlserver | postgresql
```
Rồi sửa user/password tương ứng trong `application-{profile}.yml`.

**b) Cấu hình gửi email OTP** — sửa `application.yml`:
```yaml
spring:
  mail:
    username: your-email@gmail.com
    password: your-16-char-app-password
```
**Lấy App Password Gmail**: Bật xác minh 2 bước tại tài khoản Google →
vào `myaccount.google.com/apppasswords` → tạo mật khẩu ứng dụng → copy
(bỏ dấu cách) → dán vào trên. **Không dùng được mật khẩu Gmail thường.**

**c) Cấu hình Cloudinary** — đăng ký free tại cloudinary.com, vào
Dashboard lấy `Cloud name`, `API Key`, `API Secret`, điền vào
`application.yml`:
```yaml
cloudinary:
  cloud-name: xxx
  api-key: xxx
  api-secret: xxx
```

**d) (Tuỳ chọn) Đổi JWT secret** trong `application.yml` (`app.jwt.secret`)
trước khi deploy thật.

### 4.4. Chạy project

```bash
mvn clean spring-boot:run
```

Hoặc đóng gói WAR để deploy Tomcat ngoài:
```bash
mvn clean package
# File war sinh ra tại: target/bookstore-chain-management.war
# Copy vào thư mục webapps của Tomcat
```

Truy cập: `http://localhost:8080/`

### 4.5. Tài khoản mặc định (tự sinh bởi `DataSeeder` khi khởi động lần đầu)

| Tài khoản | Mật khẩu  | Vai trò |
|-----------|-----------|---------|
| admin     | admin@123 | ADMIN (toàn quyền) |

Sau khi đăng nhập Admin → **Quản lý tài khoản** → tạo tài khoản
MANAGER/STAFF và gán vào chi nhánh mẫu ("Chi nhánh Quận 1" - tự sinh sẵn).
Khách hàng (CUSTOMER) tự đăng ký qua `/register` (cần xác thực OTP email).

---

## 5. Luồng nghiệp vụ chính

### Đăng ký + kích hoạt OTP (Session, JSP)
`/register` → tạo user `status=0` + gửi OTP email → `/verify-otp` → nhập
đúng OTP (còn hạn 5 phút) → `status=1` → `/login`.

### Đăng nhập/Đăng xuất
Spring Security form login (`/login`), tài khoản `status != 1` sẽ bị chặn
đăng nhập (hiển thị link tới `/verify-otp`). Đăng xuất: `/logout`.

### Quên mật khẩu
`/forgot-password` (nhập email) → OTP gửi email → `/reset-password` (nhập
OTP + mật khẩu mới).

### Mua hàng (Customer)
Duyệt `/books` (phân trang 6 sách/trang, lọc theo danh mục/chi nhánh/từ
khóa) → xem chi tiết `/books/{id}` → **Thêm vào giỏ** → `/cart` → `/checkout`
→ đặt hàng (tự tách đơn theo chi nhánh nếu cần) → `/my-orders`.

### Quản lý chi nhánh (Manager/Staff)
`/manager/dashboard` (widget thông báo **real-time** qua WebSocket) →
`/manager/books` (CRUD sách CHỈ của chi nhánh mình) → `/manager/orders`
(xử lý đơn hàng: Chờ xử lý → Xác nhận → Đang giao → Hoàn tất/Hủy).

### Quản trị hệ thống (Admin)
`/admin/dashboard` → quản lý Chi nhánh / Danh mục / Tài khoản (tạo
Manager/Staff) / xem toàn bộ đơn hàng.

---

## 6. Test REST API bằng JWT (Postman/curl)

**Đăng nhập lấy token:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin@123"}'
```
Response:
```json
{ "success": true, "data": { "token": "eyJhbGciOi...", "role": "ROLE_ADMIN", ... } }
```

**Gọi API công khai (không cần token):**
```bash
curl http://localhost:8080/api/books?page=0
curl http://localhost:8080/api/books/latest?limit=10
curl http://localhost:8080/api/categories
curl http://localhost:8080/api/stores
```

**Gọi API cần xác thực (đính kèm token):**
```bash
curl http://localhost:8080/api/admin/... \
  -H "Authorization: Bearer eyJhbGciOi..."
```

**Đăng ký qua API:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"khoa","password":"123456","email":"khoa@example.com","fullName":"Khoa Nguyen"}'
```

---

## 7. Test WebSocket (real-time)

1. Đăng nhập bằng tài khoản MANAGER/STAFF → vào `/manager/dashboard`.
   Trang tự động kết nối WebSocket (`/ws`, SockJS) và subscribe topic
   `/topic/store/{storeId}/orders` (xem badge "Đã kết nối real-time").
2. Mở **tab trình duyệt khác** (hoặc trình duyệt ẩn danh), đăng nhập bằng
   tài khoản CUSTOMER, mua 1 cuốn sách của **đúng chi nhánh đó** và
   checkout.
3. Quay lại tab Manager Dashboard → **Toast thông báo đơn hàng mới xuất
   hiện ngay lập tức** (không cần load lại trang), số "Thông báo chưa đọc"
   tự tăng.

---

## 8. Đổi CSDL (MySQL ⇄ SQL Server ⇄ PostgreSQL)

Chỉ cần đổi 1 dòng trong `application.yml`:
```yaml
spring:
  profiles:
    active: sqlserver     # hoặc: postgresql | mysql
```
Rồi sửa thông tin kết nối tương ứng trong file
`application-sqlserver.yml` / `application-postgresql.yml`. Không cần
sửa bất kỳ dòng code Java nào — Hibernate tự chuyển dialect và tự tạo lại
schema trên CSDL mới.

---

## 9. Các đơn giản hóa có chủ đích (ghi rõ để bảo vệ đồ án)

- `hibernate.hbm2ddl.auto=update`: phù hợp cho đề án học tập; production
  thật nên dùng Flyway/Liquibase để quản lý migration.
- `spring.jpa.open-in-view=true`: đơn giản hoá việc lazy-load quan hệ
  trong JSP/JSON; production nên tắt và dùng DTO + fetch join tường minh.
- CSRF tắt (`csrf().disable()`) để đơn giản hoá form JSP thuần; production
  nên bật lại và nhúng CSRF token vào mỗi `<form>`.
- Mật khẩu người dùng băm bằng BCrypt (an toàn), nhưng OTP lưu dạng plain
  text trong DB (chấp nhận được vì OTP có hạn dùng rất ngắn - 5 phút).
- WebSocket hiện chưa kiểm tra quyền ở mức message (ai cũng subscribe
  được nếu biết đúng URL/storeId) - có thể bổ sung
  `ChannelInterceptor`/`WebSocketSecurityConfig` để bảo mật chặt hơn.
- Đơn hàng trừ kho ngay khi đặt (COD) chứ chưa có luồng "giữ chỗ tồn kho
  tạm thời" khi đang thanh toán online.

## 10. Xử lý lỗi thường gặp

| Lỗi | Nguyên nhân | Cách khắc phục |
|------|------|------|
| `Communications link failure` khi chạy | Chưa bật MySQL / sai port | Kiểm tra MySQL service đang chạy, đúng port 3306 |
| Gửi OTP báo lỗi `535 Authentication failed` | Dùng mật khẩu Gmail thường | Tạo App Password theo mục 4.3.b |
| Upload ảnh lỗi `Invalid cloud_name` | Chưa cấu hình đúng Cloudinary | Kiểm tra lại 3 giá trị cloud-name/api-key/api-secret |
| JSP không hiển thị, lỗi 404 | Thiếu `tomcat-embed-jasper` hoặc chạy sai cách (fat jar) | Dùng `mvn spring-boot:run` khi dev, hoặc deploy WAR lên Tomcat ngoài khi production |
| WebSocket không kết nối được | Firewall/Proxy chặn WebSocket | SockJS đã có fallback polling tự động, kiểm tra Console trình duyệt (F12) |
