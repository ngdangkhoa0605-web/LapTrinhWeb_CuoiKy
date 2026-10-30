"""Generate a self-contained Word guide using Python's standard library.

Run from the project root: py docs/generate_docx.py
"""
from pathlib import Path
from zipfile import ZipFile, ZIP_DEFLATED
from xml.sax.saxutils import escape
import struct
import os

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'docs' / 'Bao-cao-hoan-chinh-Bookstore-Chain.docx'
parts = []
images = []


def paragraph(text, style=None):
    text = escape(str(text))
    props = f'<w:pPr><w:pStyle w:val="{style}"/></w:pPr>' if style else ''
    parts.append(f'<w:p>{props}<w:r><w:t xml:space="preserve">{text}</w:t></w:r></w:p>')


def heading(text):
    paragraph(text, 'Heading1')


def page_break():
    parts.append('<w:p><w:r><w:br w:type="page"/></w:r></w:p>')


def toc():
    parts.append('<w:p><w:r><w:fldChar w:fldCharType="begin"/></w:r><w:r><w:instrText xml:space="preserve"> TOC \\o &quot;1-2&quot; \\h \\z \\u </w:instrText></w:r><w:r><w:fldChar w:fldCharType="separate"/></w:r><w:r><w:t>Nhấn Ctrl+A rồi F9 để cập nhật mục lục.</w:t></w:r><w:r><w:fldChar w:fldCharType="end"/></w:r></w:p>')


def diagram(lines, caption):
    filename = f'{len([image for image in images if image[0].startswith("diagram")]) + 1:02d}'
    names = ['01-kien-truc', '02-quan-he', '03-xac-thuc', '04-tim-sach',
             '05-thanh-toan', '06-don-hang', '07-quan-ly-sach', '08-quan-tri', '09-dashboard']
    index = int(filename) - 1
    picture(ROOT / 'docs' / 'diagrams' / (names[index] + '.png'), caption, 'diagram')


def screenshot(filename, caption):
    picture(ROOT / 'docs' / 'screenshots' / filename, caption, 'screenshot')


def code_figure(filename, caption):
    picture(ROOT / 'docs' / 'code-figures' / filename, caption, 'source')


def picture(path, caption, kind):
    data = path.read_bytes()
    if data[:8] != b'\x89PNG\r\n\x1a\n':
        raise ValueError(f'Invalid PNG: {path}')
    width, height = struct.unpack('>II', data[16:24])
    image_id = len(images) + 1
    images.append((f'{kind}{image_id}.png', data))
    cx = 5486400  # 6 inches: fits A4 text width
    cy = cx * height // width
    parts.append(f'''<w:p><w:pPr><w:jc w:val="center"/><w:keepNext/></w:pPr><w:r><w:drawing><wp:inline xmlns:wp="http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing"><wp:extent cx="{cx}" cy="{cy}"/><wp:docPr id="{image_id}" name="Figure {image_id}" descr="{escape(caption)}"/><a:graphic xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main"><a:graphicData uri="http://schemas.openxmlformats.org/drawingml/2006/picture"><pic:pic xmlns:pic="http://schemas.openxmlformats.org/drawingml/2006/picture"><pic:nvPicPr><pic:cNvPr id="{image_id}" name="{escape(path.name)}"/><pic:cNvPicPr/></pic:nvPicPr><pic:blipFill><a:blip r:embed="rId{image_id + 1}" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"/><a:stretch><a:fillRect/></a:stretch></pic:blipFill><pic:spPr><a:xfrm><a:off x="0" y="0"/><a:ext cx="{cx}" cy="{cy}"/></a:xfrm><a:prstGeom prst="rect"><a:avLst/></a:prstGeom></pic:spPr></pic:pic></a:graphicData></a:graphic></wp:inline></w:drawing></w:r></w:p>''')
    paragraph(caption, 'Caption')


paragraph('BÁO CÁO ĐỒ ÁN', 'CoverLabel')
paragraph('QUẢN LÝ CHUỖI CỬA HÀNG SÁCH CŨ', 'CoverTitle')
paragraph('Tài liệu hướng dẫn xây dựng, triển khai và kiểm chứng chức năng', 'CoverSubtitle')
paragraph('Nhóm: ' + os.getenv('REPORT_GROUP', '................................'), 'CoverInfo')
paragraph('Thành viên và phân công: ' + os.getenv('REPORT_MEMBERS', '........................................................................'), 'CoverInfo')
paragraph('Giảng viên hướng dẫn: ' + os.getenv('REPORT_LECTURER', '................................'), 'CoverInfo')
paragraph('Liên kết GitHub: ' + os.getenv('REPORT_GITHUB', '................................'), 'CoverInfo')
paragraph('Năm thực hiện: 2026', 'CoverInfo')
page_break()
paragraph('MỤC LỤC', 'TocTitle')
toc()
page_break()
heading('Tóm tắt và phạm vi kiểm chứng')
paragraph('Báo cáo dựa trên mã nguồn Java/JSP và database demo của dự án. Sáu ảnh giao diện được chụp bằng Chrome từ ứng dụng chạy tại localhost:8080; đã kiểm tra HTTP 200 và xem ảnh. Phần mô tả chức năng quản trị/OTP/thanh toán dựa trên mã nguồn, CHƯA được kiểm thử giao diện end-to-end trong báo cáo này.')
paragraph('CSDL demo hiện có 111 sách, 5 danh mục và 3 chi nhánh. 100 bản ghi thư mục lấy từ Open Library, 1 trùng với sách đã có nên chỉ thêm 99 sách; giá, tồn kho và phân bổ chi nhánh là dữ liệu mô phỏng. CSDL chạy trên MariaDB tương thích MySQL; không sử dụng MongoDB Atlas.')
paragraph('Các sơ đồ dưới đây được vẽ thành hình khối và mũi tên từ luồng mã nguồn; ảnh chụp giao diện thực tế được nhúng ở mục 3.')
paragraph('Công nghệ: Java 17, Spring Boot 3.3.4, JSP/JSTL, Bootstrap 5, Spring Security, JPA, JWT, WebSocket, MySQL/SQL Server/PostgreSQL, Cloudinary.')
heading('1. Kiến trúc ứng dụng')
diagram([
    '┌ TRÌNH DUYỆT ┐   HTML/CSS/JS + JSP   ┌ SPRING MVC ┐',
    '        ↓ HTTP / Session / JWT / STOMP        ↓',
    'Controller web / api / admin / manager → Service → Repository → CSDL',
    '                          ↘ SMTP (OTP)      ↘ Cloudinary (ảnh)',
    '                          ↘ WebSocket (thông báo)' ], 'Hình 1. Kiến trúc lớp và tích hợp')
paragraph('Source: src/main/java/vn/bookstore/{controller,service,repository,entity,config,security}; giao diện: src/main/webapp/WEB-INF/views; style: src/main/resources/static. WebMvcConfig ánh xạ view JSP; SecurityConfig phân quyền; DataSeeder khởi tạo dữ liệu.')
diagram(['User → Cart → CartItem → Book → Category', 'User → Order → OrderDetail → Book → Store', 'User → Role; Notification → User'], 'Hình 2. Quan hệ nghiệp vụ (xem 10 entity để xác nhận khóa ngoại)')
heading('2. Sơ đồ hoạt động và quy trình xây dựng chức năng')
features = [
    ('Đăng ký, OTP, đăng nhập và đặt lại mật khẩu', '/register, /verify-otp, /login, /forgot-password, /reset-password', 'AuthWebController → AuthServiceImpl → UserRepository; MailService gửi OTP; SecurityConfig kiểm tra quyền.', 'Người dùng → form → kiểm tra dữ liệu → gửi OTP email → nhập OTP → kích hoạt → đăng nhập → trang theo vai trò.', 'Thử OTP sai, hết hạn, email trùng, tài khoản chưa xác minh và mật khẩu sai.'),
    ('Tra cứu sách, lọc và chi tiết', '/home, /books, /books/{id}', 'BookController → BookServiceImpl → BookRepository.searchBooks; CategoryService/StoreService cấp bộ lọc.', 'Người dùng → tìm kiếm / chọn danh mục hoặc chi nhánh → truy vấn phân trang → mở chi tiết → xem tồn kho.', 'Thử từ khóa rỗng, không có kết quả, trang âm, sách hết hàng, không có ảnh.'),
    ('Giỏ hàng và thanh toán', '/cart, /checkout', 'CartController → CartServiceImpl → CartRepository/CartItemRepository; CheckoutController → OrderServiceImpl.', 'Customer → thêm sách → sửa số lượng → kiểm tra tồn kho → nhập thông tin nhận hàng → checkout → tạo đơn và trừ kho.', 'Thử vượt số lượng tồn, giỏ trống, nhiều chi nhánh; kiểm tra dữ liệu đơn sau checkout.'),
    ('Theo dõi và xử lý đơn', '/my-orders, /admin/orders, /manager/orders', 'MyOrdersController, AdminOrderController, ManagerOrderController → OrderServiceImpl → OrderRepository.', 'Customer đặt đơn → manager xác nhận / đổi trạng thái → khách xem lịch sử → thông báo qua WebSocket.', 'Cần kiểm tra phân quyền: khách không xem đơn người khác, nhân viên chỉ xem chi nhánh được giao.'),
    ('Quản lý sách chi nhánh', '/manager/books', 'ManagerBookController → BookServiceImpl → BookRepository; CloudinaryService.uploadImage lưu URL ảnh.', 'Manager → mở form → nhập giá, số lượng, danh mục → tải ảnh (nếu có) → lưu → sách xuất hiện trong danh sách.', 'Kiểm tra định dạng dữ liệu, giá âm, ảnh rỗng, trạng thái ngừng bán.'),
    ('Quản trị chi nhánh, danh mục, người dùng', '/admin/stores, /admin/categories, /admin/users', 'AdminStoreController, AdminCategoryController, AdminUserController → StoreServiceImpl, CategoryServiceImpl, UserServiceImpl.', 'Admin → xem danh sách → tạo/sửa → service xác thực → repository lưu → danh sách cập nhật.', 'Kiểm tra tài khoản không có quyền truy cập /admin và dữ liệu tham chiếu khi xóa.'),
    ('Dashboard và API', '/admin/dashboard, /manager/dashboard, /api/books, /api/auth', 'Dashboard controllers → repositories; AuthApiController cấp JWT; BookApiController cung cấp JSON; NotificationServiceImpl phát STOMP.', 'Quản lý mở dashboard → tải thống kê → cập nhật đơn → trình duyệt nhận thông báo qua WebSocket.', 'Kiểm tra token hết hạn, endpoint được bảo vệ và mất kết nối WebSocket.'),
]
for index, (name, urls, source, flow, verify) in enumerate(features, 1):
    paragraph(f'2.{index}. {name}', 'Heading2')
    paragraph('Đường dẫn: ' + urls)
    paragraph('Hình diễn giải source code: ' + source)
    diagram([flow], f'Hình {index + 2}. Sơ đồ hoạt động: {name}')
    paragraph('Hình diễn giải giao diện: các JSP tương ứng trong src/main/webapp/WEB-INF/views/{auth,store,manager,admin}; ảnh thực tế của các trang công khai được nhúng ở mục 3. Các trang yêu cầu đăng nhập cần nhóm xác minh và chụp bổ sung.')
    paragraph('Cách kiểm chứng: ' + verify)
heading('3. Ảnh chụp giao diện từ website đang chạy')
for filename, caption in [
    ('01-trang-chu.png', 'Hình 10. Trang chủ: banner, sách mới và danh mục'),
    ('02-danh-sach-sach.png', 'Hình 11. Trang sách: tìm kiếm, lọc và phân trang'),
    ('05-chi-tiet-sach.png', 'Hình 12. Chi tiết: thông tin sách, tồn kho và giá'),
    ('06-loc-danh-muc.png', 'Hình 13. Kết quả lọc theo danh mục'),
    ('03-dang-nhap.png', 'Hình 14. Giao diện đăng nhập'),
    ('04-dang-ky.png', 'Hình 15. Giao diện đăng ký'),
]:
    screenshot(filename, caption)
heading('4. Cài đặt, database và bàn giao')
paragraph('Theo docs/SETUP.md: cài JDK 17, Maven, MySQL/MariaDB; cấu hình DB_USERNAME, DB_PASSWORD, MAIL_USERNAME, MAIL_PASSWORD, CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, CLOUDINARY_API_SECRET, JWT_SECRET qua biến môi trường. Chạy mvn clean test rồi mvn spring-boot:run. Database demo đã xuất tại database/bookstore-demo.sql; hướng dẫn restore và nguồn dữ liệu tại database/README.md. Các chức năng email/upload cần dịch vụ thật để thử nghiệm.')
heading('5. Những nội dung nhóm cần xác nhận trước khi nộp')
paragraph('Bổ sung số nhóm, danh sách thành viên, giảng viên và liên kết GitHub trên bìa; chụp thêm trang quản trị/giỏ/đặt hàng sau khi đăng nhập và kiểm thử nghiệp vụ thật. Quay video có từng thành viên, tải tài liệu/source/SQL/video lên Drive và điền sheet theo docs/HANDOFF.md. Chưa thể xác nhận hoàn tất các bước trực tuyến từ máy này.')
heading('6. Hình diễn giải mã nguồn thực tế')
paragraph('Các hình dưới đây trích trực tiếp từ ba file Java trong source hiện tại, có đường dẫn và số dòng. Phần chú giải bên phải tóm lược vai trò của đoạn mã; sơ đồ ở mục 1–2 mô tả luồng tương ứng.')
for name, caption in [
    ('01-book-controller.png', 'Hình 16. BookController: tham số lọc, phân trang và dữ liệu cho JSP'),
    ('02-checkout-controller.png', 'Hình 17. CheckoutController: kiểm tra giỏ và điều hướng sau checkout'),
    ('03-book-entity.png', 'Hình 18. Book entity: giá, số lượng, tình trạng và khóa ngoại')]:
    code_figure(name, caption)
heading('7. Datasheet AI: nguồn, vai trò và giới hạn')
paragraph('AI được dùng như công cụ hỗ trợ lập trình và biên soạn tài liệu, không phải mô hình được huấn luyện/tích hợp ra quyết định trong website. Không có tập train/test, trọng số mô hình hoặc accuracy. Không đánh đồng sơ đồ do script tạo với ảnh chụp ứng dụng.')
paragraph('Dữ liệu gồm 12 sách seed ban đầu và 100 bản ghi thư mục từ Open Library; một bản ghi trùng nên database demo có 111 sách. Open Library cung cấp tên, tác giả, năm và mã bìa. Giá, tình trạng, tồn kho và chi nhánh là dữ liệu mô phỏng; không phải giá bán crawl từ web. Tái tạo bằng database/crawl_openlibrary.py và xem database/README.md.')
paragraph('Xác minh đã thực hiện: Maven test, kiểm tra HTTP các trang công khai, SQL demo, xem ảnh chụp và kiểm tra DOCX bằng Word. Chưa xác minh OTP SMTP, upload Cloudinary và toàn bộ luồng cần đăng nhập end-to-end. Không nộp credential thật hoặc dữ liệu khách hàng; các khóa từng lộ cần được thay vòng. Datasheet chi tiết đi kèm tại docs/AI_DATASHEET.md.')
heading('8. Nhật ký prompt AI hỗ trợ sinh/chỉnh mã')
paragraph('Các prompt ghi nhận trong chính phiên làm việc này; nhóm chưa cung cấp lịch sử prompt của từng thành viên riêng nên không gán prompt cho một cá nhân cụ thể. Bản đầy đủ có đối chiếu đầu ra tại docs/PROMPTS_AI.md. Sơ đồ do script Python tạo SVG và Chrome render, không phải hình AI bên ngoài.')
for item in [
    'P1 (nguyên văn): “hoàn thiện các tính năng còn đang dang dở, sửa giao diện thành 1 cái tuyệt vời nhất đi, nhớ đừng bị lỗi placeholder các thứ” → CSS/JSP và ảnh sách dự phòng.',
    'P2 (nguyên văn): “vậy cho tôi chạy trên máy tôi trước đi” → sửa JwtUtil, build và chạy Spring Boot/MariaDB.',
    'P3 (tóm tắt theo yêu cầu kèm ảnh): thêm dữ liệu demo và sửa lỗi font footer → DemoBookSeeder, SQL, footer.',
    'P4 (tóm tắt): crawl khoảng 100 sách vào database → Open Library SQL; không kết nối MongoDB Atlas vì URI thiếu mật khẩu và ứng dụng dùng JPA.',
    'P5 (tóm tắt): chụp website, nhúng hình, thay sơ đồ chữ bằng hình vẽ, trình bày như báo cáo → ảnh/sơ đồ/DOCX.',
    'P6 (tóm tắt): bổ sung hình diễn giải mã nguồn, giao diện, datasheet AI và prompt → mục 6–8 và phụ lục kèm theo.'
]:
    paragraph(item)
heading('Tài liệu tham khảo')
paragraph('Spring Boot Documentation: https://docs.spring.io/spring-boot/ ; Spring Security Reference: https://docs.spring.io/spring-security/reference/ .')
paragraph('Open Library API: https://openlibrary.org/developers/api ; nguồn metadata sách và mã bìa. Giá/tồn kho trong báo cáo là dữ liệu demo.')
paragraph('Mã nguồn dự án và hướng dẫn triển khai: README.md, docs/SETUP.md, database/README.md.')

body = ''.join(parts) + f'<w:sectPr><w:footerReference w:type="default" r:id="rId{len(images) + 2}" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"/><w:pgSz w:w="11906" w:h="16838"/><w:pgMar w:top="1134" w:right="1134" w:bottom="1134" w:left="1417" w:footer="600"/></w:sectPr>'
document = ('<?xml version="1.0" encoding="UTF-8" standalone="yes"?>'
            '<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"><w:body>' + body + '</w:body></w:document>')
styles = '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?><w:styles xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"><w:style w:type="paragraph" w:default="1" w:styleId="Normal"><w:pPr><w:jc w:val="both"/><w:spacing w:line="360" w:lineRule="auto" w:after="120"/></w:pPr><w:rPr><w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman"/><w:sz w:val="26"/></w:rPr></w:style><w:style w:type="paragraph" w:styleId="Heading1"><w:basedOn w:val="Normal"/><w:pPr><w:keepNext/><w:spacing w:before="340" w:after="180"/><w:outlineLvl w:val="0"/></w:pPr><w:rPr><w:b/><w:color w:val="193D33"/><w:sz w:val="32"/></w:rPr></w:style><w:style w:type="paragraph" w:styleId="Heading2"><w:basedOn w:val="Normal"/><w:pPr><w:keepNext/><w:spacing w:before="240" w:after="120"/><w:outlineLvl w:val="1"/></w:pPr><w:rPr><w:b/><w:color w:val="245745"/><w:sz w:val="28"/></w:rPr></w:style><w:style w:type="paragraph" w:styleId="Caption"><w:basedOn w:val="Normal"/><w:pPr><w:jc w:val="center"/><w:spacing w:before="80" w:after="210"/></w:pPr><w:rPr><w:i/><w:sz w:val="24"/></w:rPr></w:style><w:style w:type="paragraph" w:styleId="CoverLabel"><w:basedOn w:val="Normal"/><w:pPr><w:jc w:val="center"/><w:spacing w:before="950" w:after="400"/></w:pPr><w:rPr><w:b/><w:sz w:val="32"/></w:rPr></w:style><w:style w:type="paragraph" w:styleId="CoverTitle"><w:basedOn w:val="Normal"/><w:pPr><w:jc w:val="center"/><w:spacing w:before="400" w:after="360"/></w:pPr><w:rPr><w:b/><w:color w:val="193D33"/><w:sz w:val="44"/></w:rPr></w:style><w:style w:type="paragraph" w:styleId="CoverSubtitle"><w:basedOn w:val="Normal"/><w:pPr><w:jc w:val="center"/><w:spacing w:after="1100"/></w:pPr><w:rPr><w:i/><w:sz w:val="28"/></w:rPr></w:style><w:style w:type="paragraph" w:styleId="CoverInfo"><w:basedOn w:val="Normal"/><w:pPr><w:ind w:left="900"/><w:spacing w:after="200"/></w:pPr></w:style><w:style w:type="paragraph" w:styleId="TocTitle"><w:basedOn w:val="Normal"/><w:pPr><w:jc w:val="center"/><w:spacing w:before="280" w:after="300"/></w:pPr><w:rPr><w:b/><w:sz w:val="36"/></w:rPr></w:style></w:styles>'''
with ZipFile(OUT, 'w', ZIP_DEFLATED) as z:
    z.writestr('[Content_Types].xml', '<?xml version="1.0"?><Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types"><Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/><Default Extension="xml" ContentType="application/xml"/><Default Extension="png" ContentType="image/png"/><Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/><Override PartName="/word/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"/><Override PartName="/word/footer1.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.footer+xml"/></Types>')
    z.writestr('_rels/.rels', '<?xml version="1.0"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/></Relationships>')
    relations = ['<Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>']
    for index, (name, data) in enumerate(images, 2):
        relations.append(f'<Relationship Id="rId{index}" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/image" Target="media/{name}"/>')
        z.writestr('word/media/' + name, data)
    relations.append(f'<Relationship Id="rId{len(images) + 2}" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/footer" Target="footer1.xml"/>')
    z.writestr('word/_rels/document.xml.rels', '<?xml version="1.0"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">' + ''.join(relations) + '</Relationships>')
    z.writestr('word/document.xml', document)
    z.writestr('word/styles.xml', styles)
    z.writestr('word/footer1.xml', '<?xml version="1.0"?><w:ftr xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"><w:p><w:pPr><w:jc w:val="center"/></w:pPr><w:r><w:t>Trang </w:t></w:r><w:fldSimple w:instr="PAGE"/></w:p></w:ftr>')
print(OUT)