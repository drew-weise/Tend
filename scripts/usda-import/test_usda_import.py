
import csv
import tempfile
from pathlib import Path

DATA_DIR = Path.home() / "Downloads" / "FoodData_Central_csv_2026-10-29"


def test_csv_roundtrip():
    # Tricky values that must survive an import unchanged.
    original_rows = [
        ["1", "Banana, raw", "100"],
        ["2", 'Chicken "Alfredo"', "250"],
        ["3", "Line one\nLine two", "300"],
        ["4", r"Path\with\backslashes", "400"],
        ["5", "Café crème", ""],
    ]

    with tempfile.TemporaryDirectory() as directory:
        file = Path(directory) / "test.csv"

        # Write the test data using standard CSV escaping.
        with file.open("w", encoding="utf-8", newline="") as f:
            writer = csv.writer(f)
            writer.writerows(original_rows)

        # Read it back using the same CSV rules.
        with file.open("r", encoding="utf-8", newline="") as f:
            reader = csv.reader(f, strict=True)
            imported_rows = list(reader)

        assert imported_rows == original_rows, (
            "CSV data changed during the round trip!"
        )

    print("PASS: commas")
    print("PASS: quotation marks")
    print("PASS: multiline text")
    print("PASS: backslashes")
    print("PASS: Unicode and empty values")


def test_usda_files():
    files = sorted(DATA_DIR.glob("*.csv"))

    assert len(files) == 34, (
        f"Expected 34 USDA files, found {len(files)}"
    )

    for file in files:
        with file.open("r", encoding="utf-8-sig", newline="") as f:
            reader = csv.reader(f, strict=True)
            header = next(reader)
            first_row = next(reader, None)

            assert header, f"Missing header: {file.name}"

            if first_row is not None:
                assert len(first_row) == len(header), (
                    f"Column mismatch: {file.name}"
                )

    print(f"PASS: headers and first records in {len(files)} USDA files")


if __name__ == "__main__":
    test_csv_roundtrip()
    test_usda_files()
    print("\nAll preliminary CSV tests passed!")
