"""Fetch 100 distinct public Open Library records into an idempotent MariaDB SQL import.

Run: py database/crawl_openlibrary.py
Metadata: https://openlibrary.org/developers/api (Search API). Prices/stock are DEMO ONLY.
No account credentials are accessed or saved by this script.
"""
import json
import subprocess
from pathlib import Path
from urllib.parse import urlencode

ROOT = Path(__file__).resolve().parent
OUT = ROOT / 'openlibrary-100.sql'
GROUPS = [
    ('fiction', 'Văn học', 20),
    ('business', 'Kinh tế', 20),
    ('juvenile_fiction', 'Thiếu nhi', 20),
    ('self-help', 'Kỹ năng sống', 20),
    ('science', 'Khoa học', 20),
]
HEADERS = ['Open Library metadata: title, author, first publish year and cover ID.',
           'Prices, quantities, condition and store assignments are simulated demo values, NOT crawled offers.',
           'Source: https://openlibrary.org/search.json ; covers: https://covers.openlibrary.org/']


def sql(value):
    return "'" + str(value).replace('\\', '\\\\').replace("'", "''").replace('\x00', '') + "'"


def fetch(subject, page):
    params = urlencode({'subject': subject, 'page': page, 'limit': 100,
                        'fields': 'key,title,author_name,cover_i,first_publish_year'})
    url = 'https://openlibrary.org/search.json?' + params
    result = subprocess.run(['curl.exe', '--fail', '--silent', '--show-error', '--location',
                             '--max-time', '50', url], capture_output=True, check=True)
    return json.loads(result.stdout.decode('utf-8'))['docs']


def main():
    seen = set()
    catalog = []
    for subject, category, limit in GROUPS:
        count = 0
        for page in range(1, 5):
            for item in fetch(subject, page):
                title = str(item.get('title') or '').strip()
                authors = item.get('author_name') or []
                author = str(authors[0]).strip() if authors else ''
                key = item.get('key') or ''
                cover = item.get('cover_i')
                if not (title and author and key.startswith('/works/') and isinstance(cover, int)):
                    continue
                identifier = key.rsplit('/', 1)[-1]
                if identifier in seen or len(title) > 500 or len(author) > 255:
                    continue
                seen.add(identifier)
                catalog.append((title, author, category, cover, item.get('first_publish_year'), identifier))
                count += 1
                if count == limit:
                    break
            if count == limit:
                break
        if count < limit:
            raise RuntimeError(f'Only {count}/{limit} usable books in {subject}; no partial SQL written')

    lines = ['-- ' + h for h in HEADERS]
    lines += ['SET NAMES utf8mb4;', 'START TRANSACTION;',
              "INSERT INTO categories (category_name, description, status) "
              "SELECT 'Khoa học', 'Sách khoa học tham khảo', 1 WHERE NOT EXISTS "
              "(SELECT 1 FROM categories WHERE category_name = 'Khoa học');"]
    for index, (title, author, category, cover, year, identifier) in enumerate(catalog):
        # The URL below is a real cover ID supplied by Open Library, not a local placeholder.
        image = f'https://covers.openlibrary.org/b/id/{cover}-M.jpg'
        description = ('Dữ liệu thư mục từ Open Library (năm xuất bản đầu tiên: '
                       + (str(year) if isinstance(year, int) else 'không rõ')
                       + f'). Nguồn: https://openlibrary.org/works/{identifier}. '
                         'Giá bán và tồn kho chỉ là dữ liệu mô phỏng cho bản trình diễn.')
        store_number = index % 3
        store = ['Chi nhánh Quận 1', 'Chi nhánh Hà Nội', 'Chi nhánh Đà Nẵng'][store_number]
        price = 49000 + (index % 16) * 6000
        original_price = price + 25000
        quantity = 3 + index % 11
        condition = ['GOOD', 'LIKE_NEW', 'FAIR'][index % 3]
        lines.append(
            'INSERT INTO books (title,author,description,image,price,original_price,quantity,'
            'condition_grade,status,created_date,category_id,store_id) '
            f'SELECT {sql(title)}, {sql(author)}, {sql(description)}, {sql(image)}, '
            f'{price}, {original_price}, {quantity}, {sql(condition)}, 1, NOW(), '
            f'(SELECT category_id FROM categories WHERE category_name={sql(category)} ORDER BY category_id LIMIT 1), '
            f'(SELECT store_id FROM stores WHERE store_name={sql(store)} ORDER BY store_id LIMIT 1) '
            f'WHERE NOT EXISTS (SELECT 1 FROM books WHERE title={sql(title)} AND author={sql(author)});'
        )
    lines.append('COMMIT;')
    OUT.write_text('\n'.join(lines) + '\n', encoding='utf-8')
    print(f'{len(catalog)} distinct books from Open Library -> {OUT}')
    for _, name, _ in GROUPS:
        print(name, sum(c == name for _, _, c, *_ in catalog))


if __name__ == '__main__':
    main()