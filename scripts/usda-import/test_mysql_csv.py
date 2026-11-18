
import csv
import getpass
import os
import re
import subprocess
import tempfile
from pathlib import Path

DATA_DIR = Path.home() / "Downloads" / "FoodData_Central_csv_2026-10-29"
DATABASE = "usda"
MYSQL_USER = "root"

# Set to True to reimport tables that already contain data.
FORCE_REIMPORT = False


def mysql(sql, password, local_infile=False):
    command = [
        "mysql",
        "--batch",
        "--raw",
        "--skip-column-names",
        f"--user={MYSQL_USER}",
        f"--database={DATABASE}",
    ]

    if local_infile:
        command.append("--local-infile=1")

    env = os.environ.copy()
    env["MYSQL_PWD"] = password

    result = subprocess.run(
        command,
        input=sql,
        text=True,
        capture_output=True,
        env=env,
    )

    if result.returncode != 0:
        raise RuntimeError(result.stderr.strip())

    if result.stderr.strip():
        raise RuntimeError(result.stderr.strip())

    return result.stdout.strip()


def sql_literal(value):
    return "'" + value.replace("\\", "\\\\").replace("'", "''") + "'"


def prepare_csv(source, destination):
    count = 0

    with source.open("r", encoding="utf-8-sig", newline="") as infile:
        reader = csv.reader(infile, strict=True)
        header = next(reader)
        expected_columns = len(header)

        with destination.open("w", encoding="utf-8", newline="") as outfile:
            writer = csv.writer(
                outfile,
                quoting=csv.QUOTE_ALL,
                doublequote=False,
                escapechar="\\",
                lineterminator="\n",
            )

            for row in reader:
                if len(row) != expected_columns:
                    raise ValueError(
                        f"{source.name}, record {count + 2}: "
                        f"expected {expected_columns} columns, "
                        f"found {len(row)}"
                    )

                # Preserve literal backslashes through MySQL LOAD DATA.
                escaped = [
                    value.replace("\\", "\\\\")
                    for value in row
                ]

                writer.writerow(escaped)
                count += 1

    return count


def get_columns(file):
    with file.open("r", encoding="utf-8-sig", newline="") as f:
        header = next(csv.reader(f))

    return [
        re.sub(r"[^a-z0-9]+", "_", name.strip().lower()).strip("_")
        for name in header
    ]


def import_file(file, password):
    table = file.stem
    columns = get_columns(file)

    existing = int(
        mysql(f"SELECT COUNT(*) FROM `{table}`;", password)
    )

    if existing > 0 and not FORCE_REIMPORT:
        print(
            f"  SKIPPED: {table} already contains "
            f"{existing:,} records"
        )
        return

    print(f"  Preparing {file.name}...", flush=True)

    with tempfile.TemporaryDirectory() as directory:
        prepared = Path(directory) / file.name

        expected = prepare_csv(file, prepared)

        print(f"  Validated {expected:,} records", flush=True)
        print("  Loading into MySQL...", flush=True)

        column_sql = ", ".join(f"`{column}`" for column in columns)

        # Load into an empty staging table first. The existing
        # destination data stays untouched if loading fails.
        staging = f"import_staging_{table}"

        mysql(
            f"DROP TABLE IF EXISTS `{staging}`; "
            f"CREATE TABLE `{staging}` LIKE `{table}`;",
            password,
        )

        sql = f"""
        LOAD DATA LOCAL INFILE {sql_literal(str(prepared))}
        INTO TABLE `{staging}`
        CHARACTER SET utf8mb4
        FIELDS TERMINATED BY ','
        ENCLOSED BY '"'
        ESCAPED BY '\\\\'
        LINES TERMINATED BY '\\n'
        ({column_sql});
        """

        try:
            mysql(sql, password, local_infile=True)

            actual = int(
                mysql(f"SELECT COUNT(*) FROM `{staging}`;", password)
            )

            if actual != expected:
                raise RuntimeError(
                    f"Row count mismatch for {table}: "
                    f"expected {expected:,}, got {actual:,}"
                )

            # Replace the destination only after validation succeeds.
            mysql(
                f"RENAME TABLE "
                f"`{table}` TO `{table}_old`, "
                f"`{staging}` TO `{table}`;",
                password,
            )

            mysql(f"DROP TABLE `{table}_old`;", password)

            print(f"  SUCCESS: {actual:,} records imported", flush=True)

        except Exception:
            print(f"  FAILED: {table}", flush=True)
            print(f"  Staging table retained: {staging}", flush=True)
            raise


def main():
    files = sorted(DATA_DIR.glob("*.csv"))

    if not files:
        raise FileNotFoundError(f"No CSV files found in {DATA_DIR}")

    print("=" * 50)
    print("USDA FOODDATA CENTRAL IMPORTER")
    print("=" * 50)
    print(f"Source: {DATA_DIR}")
    print(f"Database: {DATABASE}")
    print(f"Files: {len(files)}")
    print(f"Force reimport: {FORCE_REIMPORT}")
    print()

    password = getpass.getpass("MySQL password: ")

    mysql("SELECT 1;", password)

    for index, file in enumerate(files, start=1):
        print(f"\n[{index}/{len(files)}] {file.name}", flush=True)
        import_file(file, password)

    print("\n" + "=" * 50)
    print("IMPORT COMPLETE!")
    print("=" * 50)


if __name__ == "__main__":
    main()
