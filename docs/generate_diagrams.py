"""Draw report diagrams as editable SVG and render them to PNG using installed Chrome."""
from pathlib import Path
from xml.sax.saxutils import escape
import subprocess
import tempfile
import textwrap

OUT = Path(__file__).resolve().parent / 'diagrams'
CHROME = Path(r'C:\Program Files\Google\Chrome\Application\chrome.exe')
W, H = 1200, 680


def box(x, y, w, h, title, detail='', color='#245745'):
    title_lines = textwrap.wrap(title, 26, break_long_words=False) or ['']
    details = textwrap.wrap(detail, 32, break_long_words=False)[:3]
    center = x + w / 2
    lines = [f'<text x="{center}" y="{y + 43 + i * 28}" text-anchor="middle" font-size="23" font-weight="700" fill="{color}">{escape(line)}</text>' for i, line in enumerate(title_lines)]
    lines += [f'<text x="{center}" y="{y + 65 + len(title_lines) * 28 + i * 24}" text-anchor="middle" font-size="17" fill="#52645a">{escape(line)}</text>' for i, line in enumerate(details)]
    return f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="18" fill="#fff" stroke="#b7d0bd" stroke-width="2" filter="url(#shadow)"/>' + ''.join(lines)


def arrow(x1, y1, x2, y2, label=''):
    line = f'<path d="M{x1} {y1} L{x2} {y2}" fill="none" stroke="#477c65" stroke-width="4" marker-end="url(#arrow)"/>'
    if label:
        line += f'<text x="{(x1 + x2) / 2}" y="{(y1 + y2) / 2 - 14}" text-anchor="middle" fill="#315b49" font-size="16">{escape(label)}</text>'
    return line


def render(name, title, elements, note=''):
    svg = f'''<svg xmlns="http://www.w3.org/2000/svg" width="{W}" height="{H}" viewBox="0 0 {W} {H}"><defs><filter id="shadow" x="-20%" y="-20%" width="140%" height="140%"><feDropShadow dx="0" dy="3" stdDeviation="4" flood-opacity=".08"/></filter><marker id="arrow" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="8" markerHeight="8" orient="auto"><path d="M0 0 L10 5 L0 10 Z" fill="#477c65"/></marker></defs><rect width="1200" height="680" fill="#f4f7f1"/><text x="65" y="63" font-family="Arial,sans-serif" font-weight="700" font-size="32" fill="#193d33">{escape(title)}</text><path d="M65 83 H1135" stroke="#b7d0bd" stroke-width="2"/><g font-family="Arial,sans-serif">{''.join(elements)}</g><text x="65" y="640" font-family="Arial,sans-serif" font-size="17" fill="#596e61">{escape(note)}</text></svg>'''
    path = OUT / (name + '.svg')
    path.write_text(svg, encoding='utf-8')
    with tempfile.TemporaryDirectory(prefix='bookstore-diagram-') as profile:
        png = OUT / (name + '.png')
        result = subprocess.run([str(CHROME), '--headless', '--disable-gpu', '--hide-scrollbars', '--no-first-run',
                                 f'--user-data-dir={profile}', f'--window-size={W},{H}',
                                 f'--screenshot={png}', path.resolve().as_uri()], capture_output=True, timeout=45)
        if result.returncode or not png.exists() or png.stat().st_size < 10000:
            raise RuntimeError(f'Cannot render {path}: {result.stderr.decode("utf-8", "replace")[-500:]}')


def architecture():
    elements = [box(65, 130, 270, 140, 'Trình duyệt', 'JSP / Bootstrap / JavaScript'),
                box(460, 130, 275, 140, 'Spring MVC', 'Controller web / API'),
                box(865, 130, 270, 140, 'Service', 'Nghiệp vụ và phân quyền'),
                box(865, 400, 270, 130, 'Repository', 'Spring Data JPA'),
                box(460, 400, 275, 130, 'Cơ sở dữ liệu', 'MariaDB / MySQL / SQL Server'),
                box(65, 400, 270, 130, 'Dịch vụ tích hợp', 'SMTP / Cloudinary / WebSocket')]
    elements += [arrow(340, 200, 455, 200, 'HTTP'), arrow(740, 200, 860, 200),
                 arrow(1000, 275, 1000, 395), arrow(860, 465, 740, 465),
                 arrow(860, 255, 340, 455, 'Tích hợp')]
    render('01-kien-truc', 'Kiến trúc ứng dụng', elements,
           'Web dùng session; API dùng JWT. Các dịch vụ ngoài cần cấu hình riêng.')


def relationships():
    nodes = [('Role', 50, 130), ('User', 280, 130), ('Cart', 510, 130), ('CartItem', 740, 130), ('Book', 970, 130),
             ('Order', 280, 360), ('OrderDetail', 510, 360), ('Category', 740, 360), ('Store', 970, 360), ('Notification', 970, 540)]
    elements = [box(x, y, 190, 90 if label != 'Notification' else 75, label) for label, x, y in nodes]
    elements += [arrow(245, 175, 275, 175), arrow(475, 175, 505, 175), arrow(705, 175, 735, 175),
                 arrow(935, 175, 965, 175), arrow(375, 225, 375, 355), arrow(475, 405, 505, 405),
                 arrow(705, 395, 1050, 225), arrow(835, 355, 1045, 225),
                 arrow(1065, 225, 1065, 355), arrow(1065, 455, 1065, 535)]
    render('02-quan-he', 'Quan hệ thực thể chính', elements,
           'User thuộc Role; Book thuộc Category/Store; Notification thuộc Store.')


FLOWS = [
    ('03-xac-thuc', 'Đăng ký và xác thực', ['Nhập thông tin', 'Kiểm tra hợp lệ', 'Tạo user', 'Gửi OTP', 'Xác nhận OTP', 'Kích hoạt'], 'SMTP cần cấu hình để gửi OTP thật.'),
    ('04-tim-sach', 'Tìm kiếm sách', ['Từ khóa / bộ lọc', 'BookController', 'BookService', 'BookRepository', 'Phân trang', 'Xem chi tiết'], 'Lọc danh mục và chi nhánh; trả trạng thái rỗng nếu không có kết quả.'),
    ('05-thanh-toan', 'Giỏ hàng và thanh toán', ['Chọn sách', 'Thêm vào giỏ', 'Kiểm tra tồn kho', 'Nhập nhận hàng', 'Tạo đơn', 'Cập nhật kho'], 'Luồng mô tả theo mã nguồn; chưa kiểm thử end-to-end trong báo cáo.'),
    ('06-don-hang', 'Theo dõi đơn hàng', ['Đặt hàng', 'Lưu đơn', 'Quản lý xử lý', 'Cập nhật trạng thái', 'Thông báo', 'Xem lịch sử'], 'Phân quyền customer / manager / admin theo Spring Security.'),
    ('07-quan-ly-sach', 'Quản lý sách chi nhánh', ['Mở form sách', 'Nhập dữ liệu', 'Tải ảnh', 'Kiểm tra', 'Lưu vào DB', 'Hiển thị sách'], 'Ảnh tải lên Cloudinary khi dịch vụ đã cấu hình.'),
    ('08-quan-tri', 'Quản trị dữ liệu', ['Admin đăng nhập', 'Chọn chức năng', 'Thêm / sửa', 'Kiểm tra', 'Lưu dữ liệu', 'Xem danh sách'], 'Chi nhánh, danh mục và tài khoản được phân quyền ADMIN.'),
    ('09-dashboard', 'Dashboard và API', ['Mở dashboard', 'Lấy thống kê', 'Gọi API', 'Kiểm tra JWT', 'Cập nhật đơn', 'Đẩy WebSocket'], 'REST API dùng token; dashboard cập nhật thông tin theo quyền.'),
]


def flow(name, title, steps, note):
    elements = []
    for i, step in enumerate(steps):
        x = 65 + ([0, 1, 2, 2, 1, 0][i]) * 375
        y = 150 + (i // 3) * 240
        elements.append(box(x, y, 295, 120, f'{i + 1}. {step}'))
        if i > 0:
            if i == 3:
                elements.append(arrow(962, 275, 962, 385))
            else:
                previous_x = 65 + ([0, 1, 2, 2, 1, 0][i - 1]) * 375
                if x > previous_x:
                    elements.append(arrow(previous_x + 300, y + 60, x - 7, y + 60))
                else:
                    elements.append(arrow(previous_x - 7, y + 60, x + 300, y + 60))
    render(name, title, elements, note)


def main():
    OUT.mkdir(exist_ok=True)
    if not CHROME.exists():
        raise FileNotFoundError(f'Chrome not installed: {CHROME}')
    architecture()
    relationships()
    for spec in FLOWS:
        flow(*spec)
    print('Generated', len(list(OUT.glob('*.png'))), 'PNG diagrams in', OUT)


if __name__ == '__main__':
    main()