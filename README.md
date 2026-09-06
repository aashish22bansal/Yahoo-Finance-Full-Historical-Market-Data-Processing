# Yahoo Finance Full Historical Market Data Processing

A learning project for building a big-data pipeline around free historical stock market data: ingest OHLCV (Open/High/Low/Close/Volume) data from Yahoo Finance, aggregate it with Hadoop MapReduce on a Dockerized cluster, and orchestrate the whole thing from a Django control layer — with Spark feature engineering and GPU deep learning planned as the next stages.

See [ABOUT.md](ABOUT.md) for a more detailed walkthrough of the architecture and each component, or the [hosted project page](https://aashish22bansal.github.io/Yahoo-Finance-Full-Historical-Market-Data-Processing/) (live once GitHub Pages is enabled for this repo — see the note under License).

## Status

**Working today:**

- **Hadoop OHLCV aggregation job** (`app-hadoop/hadoop-finance/`) — a real Maven/Java MapReduce job that reads per-symbol daily OHLCV CSVs and produces monthly OHLCV aggregates (Open of the month, High/Low across the month, last Close, summed Volume). Runs against a Dockerized Hadoop cluster.
- **Data ingestion** — historical OHLCV CSVs pulled via `yfinance` for a handful of tickers (AAPL, AMZN, GOOGL, META, MSFT, TSLA), stored under `data/raw/yahoo_finance/`.
- **Django scaffolding** (`app-django/`) — a real Django project with apps for ingestion and a dashboard UI (AdminLTE-based), intended to become the orchestration layer that triggers Hadoop/Spark/DL jobs.

**Planned, not yet implemented:**

- Spark feature engineering (cleaning, technical indicators, time-series windowing) — the `spark/` and `etl/` scripts exist as stubs/early drafts, not wired into a pipeline yet.
- GPU deep learning (LSTM/Transformer price forecasting, volatility modeling) — `dl/` is currently empty.
- Django-triggered job orchestration (subprocess/Celery calls into Spark and Hadoop) — the Django apps for this exist but contain mostly boilerplate so far.

This is a personal learning project, not a production system — the goal is to understand each layer (Hadoop, Spark, Django, GPU training) by building it, not to ship a finished trading platform.

## Architecture

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

Django is meant to sit alongside this pipeline as a control plane — triggering each stage and reporting status/logs — rather than processing data itself.

## Tech stack

- **Hadoop 3.x** (MapReduce) — batch OHLCV aggregation
- **Docker** — a cloned [big-data-europe](https://github.com/big-data-europe) Hadoop cluster (`docker/docker-hadoop/`) for running the MapReduce job without a real multi-node setup
- **Java 17 / Maven** — the Hadoop job (`app-hadoop/hadoop-finance/`)
- **Python / Django** — ingestion and (planned) orchestration UI (`app-django/`)
- **yfinance, pandas** — data acquisition
- **Spark, PyTorch/TensorFlow** — declared as dependencies for the planned feature-engineering and deep-learning stages

## Repository structure

```
├── app-hadoop/hadoop-finance/   Maven project — the OHLCV MapReduce job (working)
├── app-django/                  Django project — ingestion + dashboard UI (working, orchestration WIP)
├── docker/                      Cloned big-data-europe images (docker-hadoop, docker-spark, ...)
├── data/                        Raw and processed CSVs
├── etl/                         Early Spark/pandas ETL scripts (clean, convert to parquet, feature eng.)
├── spark/                       Spark job entry points (stub — not wired up yet)
├── dl/                          Deep learning models/training scripts (empty — planned)
├── config/                      Spark/training config stubs
├── scripts/                     One-off utilities (e.g. parsing Hadoop job output)
├── utils/                       Controller stubs Django will use to trigger jobs
└── archieve/                    Older backup of the Hadoop Java classes, kept for reference
```

## Getting started

**1. Start the Hadoop cluster**

```bash
cd docker/docker-hadoop
docker compose up -d
```

**2. Build the Hadoop job**

```bash
cd app-hadoop/hadoop-finance
mvn clean package
```

This produces a shaded jar with `com.aashish22bansal.hadoop.financial.mapreduce.OHLCVDriver` as the entry point.

**3. Run the OHLCV aggregation job**

Copy the built jar and an input CSV into the `namenode` container, then run:

```bash
docker cp target/hadoop-finance-<version>.jar namenode:/tmp/
docker exec -it namenode hadoop jar /tmp/hadoop-finance-<version>.jar \
    com.aashish22bansal.hadoop.financial.mapreduce.OHLCVDriver \
    /input/path /output/path
```

See `Steps - Docker.txt` for the exact commands used during development.

**4. Run the Django app**

```bash
cd app-django
python -m venv venv
venv\Scripts\activate      # Windows
pip install -r requirements.txt
python manage.py migrate
python manage.py runserver
```

## Roadmap

- Validate the OHLCV monthly aggregation output against known values for a few tickers
- Partition MapReduce output by symbol and date, with a secondary sort on date within each symbol
- Wire the Spark scripts in `etl/` into an actual pipeline (raw CSV → cleaned → features → windowed)
- Have Django trigger the Hadoop and Spark jobs instead of running them manually
- Build a first deep learning model (LSTM) on the processed output

## License

Custom Attribution-Required License — any use, modification, or deployment of this project (including as a service) must give clear, visible credit to the original author and link back to this repository. See [LICENSE](LICENSE) for the full terms.

## Hosted project page

A GitHub Actions workflow (`.github/workflows/pages.yml`) renders [ABOUT.md](ABOUT.md) to a static page and deploys it to GitHub Pages on every push to `main`. One-time setup required: in this repo's Settings → Pages, set "Source" to "GitHub Actions".
