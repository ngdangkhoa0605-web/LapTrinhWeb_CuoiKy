"""Enrich existing Open Library books; never overwrite descriptions without source IDs.

Uses Python standard library and the MySQL CLI. MYSQL_CLIENT and MYSQL_PWD
may be supplied through environment variables. Run after importing the catalog.
Descriptions are source excerpts, not full book texts. Results are cached as SQL.
"""
import json
import os
import re
import subprocess
import time
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parent


def main():
    client = os.environ.get('MYSQL_CLIENT', 'mysql')
    args = [client, '--protocol=tcp', '-h', '127.0.0.1', '-P',
            os.environ.get('MYSQL_PORT', '3307'), '-u',
            os.environ.get('MYSQL_USER', 'root'), '--default-character-set=utf8mb4',
            '--batch', '--skip-column-names', 'bookstore']
    result = subprocess.run(args + ['-e', 'SELECT book_id,description FROM books'],
                            capture_output=True, check=True)
    updates = []
    for line in result.stdout.decode('utf-8').splitlines():
        book_id, _, description = line.partition('\t')
        match = re.search(r'https://openlibrary.org/works/(OL\d+W)', description)
        if not match:
            continue
        identifier = match.group(1)
        try:
            request = urllib.request.Request(
                f'https://openlibrary.org/works/{identifier}.json',
                headers={'User-Agent': 'BookstoreStudentCatalog/1.0'})
            with urllib.request.urlopen(request, timeout=12) as response:
                data = json.load(response)
            text = data.get('description', '')
            if isinstance(text, dict):
                text = text.get('value', '')
            if not text:
                subjects = data.get('subjects', [])[:8]
                if subjects:
                    text = 'Chủ đề được Open Library phân loại: ' + ', '.join(subjects) + '.'
            if text:
                text = text[:6000] + f'\nNguồn: https://openlibrary.org/works/{identifier}. Giá và tồn kho là dữ liệu demo.'
                encoded = text.encode('utf-8').hex()
                updates.append(f"UPDATE books SET description=CONVERT(0x{encoded} USING utf8mb4) WHERE description LIKE '%https://openlibrary.org/works/{identifier}.%';")
        except Exception as error:
            print(f'{identifier}: {type(error).__name__}', flush=True)
        time.sleep(1)
    output = ROOT / 'openlibrary-descriptions.sql'
    output.write_text('SET NAMES utf8mb4;\nSTART TRANSACTION;\n' + '\n'.join(updates) + '\nCOMMIT;\n', encoding='utf-8')
    subprocess.run(args, input=output.read_bytes(), check=True)
    print(f'Updated {len(updates)} descriptions; catalog count unchanged.', flush=True)


if __name__ == '__main__':
    main()