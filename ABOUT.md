# About this project

## Why this exists

This is a personal learning project. The goal isn't to ship a production trading platform — it's to build hands-on understanding of a distributed data-processing stack by actually building each layer: Hadoop MapReduce for batch aggregation, Spark for feature engineering, Django as an orchestration layer, and GPU-based deep learning for forecasting. Each piece is deliberately built by hand rather than through a managed service, because the point is to understand *how* the pieces work, not just to get an answer out of them.

Yahoo Finance's historical OHLCV (Open/High/Low/Close/Volume) data is a good fit for this: it's free, it's naturally time-series shaped, it scales from a handful of tickers up to thousands (and tens of gigabytes) without changing the problem, and the eventual outputs (aggregates, features, forecasts) are easy to sanity-check against public knowledge of how a stock actually behaved.

## Architecture, and why each stage exists

```
Yahoo Finance
     │  yfinance
     ▼
Django ingestion  ──────────────────▶  data/raw/  (CSV, per ticker)
                                            │
                                            ▼
                                   HDFS (Dockerized cluster)
                                            │
                                            ▼
                          Hadoop MapReduce — OHLCV monthly aggregation   ✅ working
                                            │
                                            ▼
                                   HDFS (processed output)
                                            │
                                            ▼
                     Spark — cleaning, feature engineering, windowing    🚧 planned
                                            │
                                            ▼
                GPU deep learning — LSTM / Transformer forecasting       🚧 planned
```

- **Hadoop MapReduce for aggregation.** Turning a few million rows of daily OHLCV data into monthly aggregates per ticker is exactly the kind of embarrassingly-parallel, map-then-reduce problem MapReduce was designed for — each mapper handles one file/split, a combiner does partial aggregation to cut network shuffle, and the reducer produces one row per symbol-month. It's also the most direct way to learn what Hadoop is actually doing under the hood, rather than treating it as a black box.
- **Spark for feature engineering (planned).** Once data is aggregated, the next step — computing technical indicators, log returns, rolling volatility, and turning a time series into fixed-size windows for a model — is naturally expressed as DataFrame transformations, which Spark handles far more ergonomically than raw MapReduce.
- **Django as the control plane.** Django is not meant to process data itself. Its job is to trigger the Hadoop/Spark/DL jobs, track their status, and present results — keeping heavy computation out of the request/response cycle entirely.
- **GPU only for deep learning (planned).** Training an LSTM or Transformer on the processed, windowed dataset is the one stage that actually benefits from a GPU; every earlier stage stays CPU-bound, which also keeps the whole pipeline runnable on a single laptop with a fixed memory budget.

## Components

### `app-hadoop/hadoop-finance/`

A Maven project (`com.aashish22bansal.hadoop`, artifact `hadoop-finance`) containing the working part of this project: a MapReduce job under `com.aashish22bansal.hadoop.financial.mapreduce` that aggregates daily OHLCV CSVs into monthly summaries per ticker.

- `OHLCVMapper` — parses each CSV row, emits `(symbol|month, OHLCV)`
- `OHLCVCombiner` — partial aggregation per map task, to reduce shuffle volume
- `OHLCVReducer` — final aggregation: month's Open (first), High (max), Low (min), Close (last), Volume (sum)
- `OHLCVWritable` — the custom Hadoop `Writable` carrying an OHLCV record between map and reduce
- `TickerPartitioner` — custom partitioner
- `OHLCVDriver` / `FinancialOHLCVJob` — job driver, wired up with the Maven Shade plugin to produce a runnable uber-jar

It builds and runs against the Dockerized Hadoop cluster in `docker/docker-hadoop/` (a cloned [big-data-europe](https://github.com/big-data-europe) setup using `bde2020/hadoop-*` images) — see the README's "Getting started" section for the exact commands.

### `app-django/`

A real Django project (`manage.py`, `webui/` settings) with six apps registered: `app_hadoop`, `app_ingest`, `app_ui`, `app_spark`, `app_dl`, `app_monitor`. Currently:

- `app_ingest` has working logic — `download.py` and `services/data_loader.py` pull historical OHLCV data via `yfinance` for a set of tickers.
- `app_ui` has a working dashboard shell (`plotting.py`, an AdminLTE-themed template) for eventually visualizing pipeline output.
- The remaining apps (`app_hadoop`, `app_spark`, `app_dl`, `app_monitor`) are scaffolded but not yet wired to actually trigger jobs — this is the next major piece of orchestration work.

### `docker/`

Vendored clones of [big-data-europe](https://github.com/big-data-europe)'s Docker images for Hadoop, Spark, Hive, HBase, Kafka, Flink, Cassandra, and a few others. Only `docker-hadoop/` is actively used right now, to run a single-node-style Hadoop cluster (namenode, datanode, resourcemanager, nodemanager, historyserver) for the MapReduce job above. The rest are there for when Spark/other components get wired in.

### `etl/`, `spark/`, `dl/`, `config/`, `scripts/`, `utils/`

Early-stage and stub directories for the planned next layers:

- `etl/` — draft Python/pandas scripts (clean_data, convert_to_parquet, feature_engineering, create_windows) not yet assembled into a pipeline
- `spark/` — a placeholder for Spark job entry points
- `dl/` — currently empty; will hold model definitions and training scripts
- `config/` — Spark and training config stubs
- `scripts/` — small utilities (e.g. parsing Hadoop job output)
- `utils/` — controller stubs Django will eventually use to invoke Hadoop/Spark/DL jobs as subprocesses

## Current status vs. roadmap

**Working today:** OHLCV MapReduce aggregation on a Dockerized Hadoop cluster; `yfinance`-based data ingestion; a Django project with a working ingestion app and dashboard shell.

**Next up:**
- Validate the MapReduce aggregation output against known values for a few tickers
- Partition MapReduce output by symbol and date, with a secondary sort on date within each symbol
- Wire the `etl/` scripts into an actual Spark pipeline
- Have Django trigger the Hadoop and Spark jobs instead of running them manually
- Train a first deep learning model (LSTM) on the processed output

## License

This project is licensed under a custom Attribution-Required License (see [LICENSE](LICENSE)) — any use, modification, or deployment of this project, including as a hosted service, must give clear, visible credit to the original author and link back to this repository.

## Author

Aashish Bansal — [github.com/aashish22bansal](https://github.com/aashish22bansal)
