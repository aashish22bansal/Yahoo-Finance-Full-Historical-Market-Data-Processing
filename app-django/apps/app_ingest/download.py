import yfinance as yf
import pandas as pd
from pathlib import Path

OUTPUT_DIR = Path("..\\..\\..\\data\\raw\\yahoo_finance")
OUTPUT_DIR.mkdir(exist_ok=True)

def download_ticker(ticker):
    df = yf.download(
        ticker,
        start="2000-01-01",
        progress=False
    )
    df.reset_index(inplace=True)
    df["Ticker"] = ticker

    df = df[[
        "Date", "Ticker",
        "Open", "High", "Low", "Close", "Volume"
    ]]

    df.to_csv(OUTPUT_DIR / f"{ticker}.csv", index=False)

if __name__ == "__main__":
    with open("..\\..\\utils\\tickers.txt") as f:
        for line in f:
            download_ticker(line.strip())
