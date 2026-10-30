# Bàn giao cơ sở dữ liệu

**Chạy trên máy khác:** dùng bản xuất MySQL `bookstore-mysql.sql` và làm theo [RUN-ON-ANOTHER-PC.md](RUN-ON-ANOTHER-PC.md). Bản xuất có 111 sách cùng mô tả, 5 danh mục, 3 chi nhánh, cấu trúc đầy đủ; không có dữ liệu người dùng/đơn hàng. Không cần import các dataset tăng thêm sau khi nhập bản này.

## Cấu hình thực tế cập nhật 30/10/2026

Website chạy bằng **MySQL Community 8.4.8**, host `127.0.0.1`, port `3307`, database `bookstore`. MariaDB cũ ở port 3306 được giữ lại làm bản dự phòng, không phải database của lệnh chạy `start-springboot.cmd` tại thư mục gốc. Không sử dụng MongoDB.

Đã chuyển toàn bộ database cũ sang MySQL, giữ 111 sách và các quan hệ hiện có. Tài khoản ứng dụng `bookstore_app` chỉ được cấp quyền trên database `bookstore`; mật khẩu lưu mã hóa theo Windows tại `%LOCALAPPDATA%/BookstoreChain/mysql.xml`, không commit vào Git.

`enrich_descriptions.py` lấy phần mô tả tác phẩm từ Open Library, hoặc danh sách chủ đề khi tác phẩm không có mô tả, ghi thành `openlibrary-descriptions.sql` và cập nhật sách đã có. Đây là giới thiệu/metadata, **không phải toàn văn sách**. Giá và tồn kho vẫn là dữ liệu mô phỏng. Script dùng thư viện chuẩn Python; cấu hình `MYSQL_CLIENT`, `MYSQL_PORT`, `MYSQL_USER`, `MYSQL_PWD` trước khi chạy nếu không dùng mặc định. Các bản ghi nguồn không có thông tin hoặc lỗi mạng được giữ nguyên.

Các hướng dẫn MariaDB bên dưới là lịch sử bộ dữ liệu cũ. Với MySQL trên máy này, dùng script `start-springboot.cmd` tại thư mục gốc, không dùng các script MariaDB cũ trong `docs`.

`bookstore-demo.sql` là bản xuất database demo đã chạy thực tế trên MariaDB cục bộ: **3 chi nhánh, 5 danh mục, 111 sách** (12 sách demo ban đầu + 99 sách mới không trùng), các bảng nghiệp vụ và tài khoản admin khởi tạo. Có thể import vào database trống `bookstore` bằng `mariadb -u root -p bookstore < database/bookstore-demo.sql` (tạo database trước). Lưu ý file chứa tài khoản admin demo với mật khẩu mặc định; chỉ dùng trong môi trường học tập, phải đổi mật khẩu nếu triển khai công khai. Sau khi import, không bật seed demo nếu muốn giữ dữ liệu nguyên trạng.

`openlibrary-100.sql` là **bản nhập tăng thêm**: 100 bản ghi metadata lấy từ Open Library (20 bản ghi mỗi nhóm: Văn học, Kinh tế, Thiếu nhi, Kỹ năng sống, Khoa học); một cuốn đã tồn tại trong 12 sách nên SQL chỉ thêm 99 cuốn vào database demo. File được sinh bằng `py database/crawl_openlibrary.py`, nhập vào database đang có đủ chi nhánh/danh mục bằng `py database/import_local.py` trên máy đã thiết lập MariaDB tại đường dẫn tương ứng. **Không nhập `openlibrary-100.sql` sau khi đã nhập `bookstore-demo.sql`** trừ khi muốn kiểm tra tính chống trùng (SQL sẽ bỏ qua sách cùng tiêu đề + tác giả). Dữ liệu thư mục và mã bìa từ Open Library; **giá, tồn kho, tình trạng và phân bổ chi nhánh là dữ liệu mô phỏng**, không phải giá/chào bán được crawl. Ảnh bìa phụ thuộc vào dịch vụ covers.openlibrary.org khi có Internet.

Ứng dụng mặc định dùng MySQL 8, database `bookstore`; Hibernate tạo bảng từ 10 entity khi chạy lần đầu (`spring.jpa.hibernate.ddl-auto=update`). `DataSeeder` tạo vai trò, chi nhánh, danh mục và tài khoản quản trị. Không được coi một file SQL tự viết từ entity là bản sao dữ liệu thực tế.

Để nộp **Database (.sql)** chính xác, trên máy đã chạy ứng dụng và có MySQL, xuất bằng:

```powershell
mysqldump --single-transaction --default-character-set=utf8mb4 -u root -p bookstore > database/bookstore.sql
```

Kiểm tra file vừa tạo có `CREATE TABLE` và `INSERT INTO`, sau đó kiểm thử khôi phục vào database trống:

```powershell
mysql -u root -p -e "CREATE DATABASE bookstore_restore CHARACTER SET utf8mb4;"
mysql -u root -p bookstore_restore < database/bookstore.sql
```

**Trước khi tải lên Drive:** kiểm tra/xóa email, số điện thoại, mật khẩu hash và dữ liệu khách hàng trong bản xuất; tốt nhất dùng database demo không có dữ liệu thật. Bản `bookstore-demo.sql` đã được xuất từ MariaDB đang chạy trên máy này nhưng **chưa kiểm thử restore vào database mới**. Project hiện dùng JPA SQL, **không kết nối MongoDB Atlas**; URI MongoDB thiếu mật khẩu không thể được dùng để import vào ứng dụng này.