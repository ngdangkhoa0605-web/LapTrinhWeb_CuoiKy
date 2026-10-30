"""Import the generated Open Library SQL into the already-running local demo database."""
import subprocess
from pathlib import Path

sql = Path(__file__).with_name('openlibrary-100.sql')
client = Path(r'C:\Program Files\MariaDB 12.2\bin\mariadb.exe')
with sql.open('rb') as stream:
    result = subprocess.run([str(client), '--protocol=tcp', '-h', '127.0.0.1',
                             '-u', 'root', '--default-character-set=utf8mb4', 'bookstore'],
                            stdin=stream, capture_output=True)
print(result.stderr.decode('utf-8', 'replace'))
print('Import exit code:', result.returncode)
raise SystemExit(result.returncode)