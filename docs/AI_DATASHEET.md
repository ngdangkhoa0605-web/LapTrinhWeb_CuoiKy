# Datasheet AI – dự án Bookstore Chain Management

## Phạm vi
AI hỗ trợ rà soát mã Spring Boot/JSP, sửa lỗi biên dịch `JwtUtil`, thiết kế CSS/JSP, tạo dữ liệu demo và tài liệu. Đây không phải mô hình AI được huấn luyện hay tích hợp để tự động ra quyết định trong website; không có bộ trọng số, tập train/validation/test hoặc chỉ số accuracy để báo cáo.

## Dữ liệu và nguồn
12 cuốn demo khởi tạo bằng mã `DemoBookSeeder`; 100 metadata sách từ Open Library Search API, một cuốn trùng nên database demo tổng cộng 111 cuốn. Thuộc tính lấy từ API: tên sách, tác giả, năm xuất bản đầu tiên, mã ảnh bìa và work ID. Giá, tồn kho, tình trạng và chi nhánh do script tạo **mô phỏng** để thử chức năng; không đại diện giá bán thực tế. Ảnh bìa lấy từ covers.openlibrary.org và có thể phụ thuộc kết nối mạng. Nguồn và cách tái tạo: `database/crawl_openlibrary.py`, `database/openlibrary-100.sql`, `database/README.md`.

## Phương pháp sinh tài liệu
`docs/generate_diagrams.py` sinh SVG từ luồng mã nguồn và render PNG bằng Chrome; `docs/generate_code_figures.py` lấy đoạn Java thật theo đường dẫn/dòng và tạo hình chú thích; `docs/capture-screenshots.ps1` chụp sáu trang công khai từ localhost; `docs/generate_docx.py` nhúng các hình vào báo cáo. Không coi sơ đồ hoặc hình chú thích mã là ảnh chụp giao diện thực tế.

## Xác minh và giới hạn
Đã kiểm tra `mvn test`, HTTP 200 cho các trang công khai, số bản ghi database, ảnh PNG và DOCX trong Word. Chưa có xác minh end-to-end cho OTP SMTP, upload Cloudinary, thanh toán, phân quyền quản trị bằng tài khoản thật; các sơ đồ ở đó là diễn giải mã nguồn chứ không phải bằng chứng đã chạy. Người dùng cần xem lại nội dung AI sinh và bổ sung ảnh thử nghiệm của các luồng này nếu giảng viên yêu cầu.

## Bảo mật, đạo đức và quản trị
Không đưa mật khẩu, khóa API, JWT secret thật hoặc URI chứa credential vào prompt/ZIP. Thay vòng các credential từng lộ trong source cũ. Chỉ xuất database demo, không xuất dữ liệu cá nhân thực. Các prompt được ghi trong `docs/PROMPTS_AI.md`; những prompt không có nhật ký không được bịa thành lời nhóm đã nhập.