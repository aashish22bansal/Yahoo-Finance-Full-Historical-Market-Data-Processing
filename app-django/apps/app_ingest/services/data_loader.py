import pandas as pd
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parents[4]
DATA_FILE = BASE_DIR / "data" / "processed" / "yahoo_monthly.csv"

def load_monthly_data():
    if not DATA_FILE.exists():
        # print(BASE_DIR)
        # print(DATA_FILE)
        raise FileNotFoundError("Processed data not found. Run Hadoop parser first.")
    return pd.read_csv(DATA_FILE)
