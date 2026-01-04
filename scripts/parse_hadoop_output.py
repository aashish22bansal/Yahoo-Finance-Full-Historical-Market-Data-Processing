import pandas as pd
from pathlib import Path

INPUT_DIR = Path("data/processed/yahoo_output")
OUTPUT_FILE = "data/processed/yahoo_monthly.csv"

rows = []

for part_file in INPUT_DIR.glob("part-*"):
    with open(part_file, "r") as f:
        for line in f:
            line = line.strip()
            if not line:
                continue

            # Split key and value by TAB
            key, value_str = line.split("\t", 1)

            ticker, month = key.split("|")

            # Parse key=value pairs
            fields = {}
            for item in value_str.split(","):
                k, v = item.strip().split("=")
                fields[k] = v

            rows.append({
                "ticker": ticker,
                "month": month,
                "open": float(fields["Open"]),
                "high": float(fields["High"]),
                "low": float(fields["Low"]),
                "close": float(fields["Close"]),
                "volume": int(fields["Volume"]),
                "vwap": float(fields["VWAP"]),
            })

df = pd.DataFrame(rows)

print("Total parsed rows:", len(rows))
if rows:
    print("Sample row:", rows[0])

# Sort for charts & APIs
df.sort_values(["ticker", "month"], inplace=True)

df.to_csv(OUTPUT_FILE, index=False)

print(f"Parsed {len(df)} rows")
print(df.head())
