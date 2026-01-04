from pathlib import Path
import pandas as pd

BASE_DIR = Path(__file__).resolve().parents[2]  
# points to Yahoo-Finance-Full-Historical-Market-Data-Processing/

INPUT_ROOT = BASE_DIR / "data" / "processed"
OUTPUT_CSV = INPUT_ROOT / "yahoo_monthly.csv"

def parse_hadoop_output():
    rows = []

    for part_file in INPUT_ROOT.rglob("part-*"):
        with open(part_file, "r") as f:
            for line in f:
                line = line.strip()
                if not line:
                    continue

                # AAPL|2000-01\tOpen=..., High=..., ...
                key, value_str = line.split("\t", 1)
                ticker, month = key.split("|")

                fields = {}
                for kv in value_str.split(","):
                    k, v = kv.strip().split("=")
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

    if not rows:
        raise RuntimeError("No Hadoop output parsed. Check data/processed path.")

    df = pd.DataFrame(rows)
    df.sort_values(["ticker", "month"], inplace=True)
    df.to_csv(OUTPUT_CSV, index=False)

    return df
