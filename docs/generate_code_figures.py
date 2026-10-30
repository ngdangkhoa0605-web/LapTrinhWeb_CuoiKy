"""Render annotated excerpts of actual Java source for the report using Chrome."""
from pathlib import Path
import subprocess
import tempfile
from html import escape

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'docs' / 'code-figures'
CHROME = Path(r'C:\Program Files\Google\Chrome\Application\chrome.exe')
SOURCES = [
    ('01-book-controller', 'BookController.java', 'controller/web/BookController.java', 30, 50,
     'Bộ lọc và phân trang', 'Nhận page/keyword/categoryId/storeId, chặn trang âm, gọi service và truyền kết quả sang JSP.'),
    ('02-checkout-controller', 'CheckoutController.java', 'controller/web/CheckoutController.java', 29, 53,
     'Xử lý thanh toán', 'Giỏ rỗng quay về /cart; checkout tạo đơn rồi chuyển /my-orders, thông báo lỗi nghiệp vụ.'),
    ('03-book-entity', 'Book.java', 'entity/Book.java', 48, 84,
     'Cấu trúc sách', 'Giá, tồn kho, tình trạng cùng liên kết Category/Store là dữ liệu của mỗi đầu sách.'),
]


def build(name, title, relative, start, end, label, explanation):
    path = ROOT / 'src/main/java/vn/bookstore' / relative
    lines = path.read_text(encoding='utf-8').splitlines()
    if len(lines) < end:
        raise ValueError(f'Source too short: {path}')
    source = '\n'.join(f'{i:>3}  {lines[i - 1]}' for i in range(start, end + 1))
    html = f'''<!doctype html><html lang="vi"><head><meta charset="utf-8"><style>
    *{{box-sizing:border-box}}body{{margin:0;background:#f4f7f1;color:#213c32;font-family:Arial,sans-serif}}
    header{{padding:30px 48px;background:#193d33;color:white;font-size:29px;font-weight:bold}}
    .path{{padding:14px 48px;color:#65746b;font-size:17px}}.layout{{display:flex;gap:22px;margin:10px 42px}}
    pre{{flex:1;min-width:0;margin:0;padding:23px;background:#172923;color:#e7f4e7;border-radius:14px;
    font:16px/1.56 Consolas,monospace;white-space:pre-wrap;overflow:hidden}}
    aside{{width:285px;padding:24px;border-radius:14px;background:white;border:2px solid #c5d9c8;
    font-size:20px;line-height:1.5}}aside b{{color:#245745}}footer{{padding:20px 46px;color:#65746b;font-size:16px}}
    </style></head><body><header>{escape(title)}</header><div class="path">src/main/java/vn/bookstore/{escape(relative)} · dòng {start}–{end}</div>
    <div class="layout"><pre>{escape(source)}</pre><aside><b>{escape(label)}</b><p>{escape(explanation)}</p>
    <p>Trích trực tiếp từ source của dự án; số dòng khớp bản hiện tại.</p></aside></div>
    <footer>Hình diễn giải mã nguồn · Java / Spring Boot</footer></body></html>'''
    OUT.mkdir(exist_ok=True)
    page = OUT / f'{name}.html'
    png = OUT / f'{name}.png'
    page.write_text(html, encoding='utf-8')
    with tempfile.TemporaryDirectory(prefix='bookstore-code-') as profile:
        result = subprocess.run([str(CHROME), '--headless', '--disable-gpu', '--hide-scrollbars', '--no-first-run',
                                 f'--user-data-dir={profile}', '--window-size=1440,1100',
                                 f'--screenshot={png}', page.resolve().as_uri()], capture_output=True, timeout=45)
    if result.returncode or not png.exists() or png.stat().st_size < 15000:
        raise RuntimeError(f'Cannot render {page}: {result.stderr.decode("utf-8", "replace")[-400:]}')


if __name__ == '__main__':
    for item in SOURCES:
        build(*item)
    print(f'Generated {len(SOURCES)} annotated source figures in {OUT}')