# Yahoo Finance Full Historical Market Data Processing

> A learning project that pulls free historical stock market data from Yahoo Finance and aggregates it with Hadoop MapReduce on a Dockerized cluster — with Spark feature engineering and GPU deep learning forecasting as the next stages.

**Type:** Data Engineering / Distributed Systems (learning project)
**Status:** Active, early-stage — one pipeline stage fully working, the rest in progress
**Difficulty:** Intermediate
**Primary Goal:** Learn Hadoop, Spark, and a GPU deep-learning workflow by building each layer directly, using real historical market data as the working example

| Aspect       | Details                                                                 |
| ------------ | ------------------------------------------------------------------------ |
| Problem      | Historical OHLCV data across many tickers is tedious to aggregate by hand |
| Users        | The author, as a hands-on way to learn distributed data processing       |
| Input        | Daily OHLCV CSVs per ticker, pulled from Yahoo Finance via `yfinance`     |
| Processing   | Hadoop MapReduce aggregation today; Spark features and GPU forecasting planned |
| Output       | Monthly OHLCV aggregates per ticker (today); model forecasts (planned)   |
| Technologies | Java/Hadoop/Maven, Docker, Python/Django, yfinance/pandas                |
| Architecture | Batch                                                                     |
| Scale        | A handful of tickers today; designed to scale to thousands               |

**If you only have 30 seconds:** this project takes daily stock price CSVs, aggregates them into monthly OHLCV summaries using a hand-written Hadoop MapReduce job running on a Dockerized cluster, and is being extended with a Spark feature-engineering stage and a GPU deep-learning forecasting stage.

---

## 1. The Problem

Yahoo Finance makes decades of daily OHLCV (Open/High/Low/Close/Volume) data available for free, for thousands of tickers. That's exactly the kind of dataset that's simple to reason about at a small scale but tedious and error-prone to process by hand once you're looking at years of data across more than a few symbols:

```
RAW DATA
   │
   ├── one CSV per ticker, per year of history
   ├── every row is daily, not aggregated
   ├── manually computing monthly Open/High/Low/Close/Volume per ticker
   │   doesn't scale past a handful of symbols
   └── doing it by hand also teaches you nothing about distributed processing
            │
            ▼
   HARD TO AGGREGATE, AND NOT A LEARNING OPPORTUNITY IF YOU SCRIPT IT IN PANDAS ALONE
```

## 2. The Idea / Solution

Rather than reach for pandas and call it done, the project deliberately routes the aggregation step through Hadoop MapReduce running on a real (if single-node, Dockerized) cluster — because the point of this project is to learn the mechanics of distributed batch processing, not just get the aggregated numbers.

```
                ┌───────────────┐
                │ Yahoo Finance │
                └───────┬───────┘
                        ▼
                ┌───────────────┐
                │  Ingestion    │  yfinance, per ticker
                └───────┬───────┘
                        ▼
                ┌───────────────┐
                │     HDFS      │  raw CSV storage
                └───────┬───────┘
                        ▼
                ┌───────────────┐
                │ MapReduce Job │  monthly OHLCV aggregation   ✅ working
                └───────┬───────┘
                        ▼
                ┌───────────────┐
                │     HDFS      │  processed output
                └───────┬───────┘
                        ▼
                ┌───────────────┐
                │     Spark     │  feature engineering          🚧 planned
                └───────┬───────┘
                        ▼
                ┌───────────────┐
                │  GPU Deep     │  LSTM / Transformer forecasting  🚧 planned
                │  Learning     │
                └───────────────┘
```

## 3. Project Story

```
v0.0 — Hadoop WordCount
   │   A stock Spring/Maven WordCount example, imported purely to get a
   │   working Hadoop + Maven + JDK toolchain running locally.
   ↓
v0.1 — Repurposed for finance
   │   WordCount's mapper/reducer replaced with OHLCV-specific logic;
   │   package renamed to com.aashish22bansal.hadoop.financial.mapreduce.
   ↓
v0.2 — Dockerized cluster
   │   Adopted a cloned big-data-europe Hadoop Docker Compose setup so the
   │   job runs against namenode/datanode/resourcemanager containers
   │   instead of a manual local Hadoop install.
   ↓
v0.3 — Data ingestion + Django scaffolding
   │   yfinance-based ingestion script pulling real ticker CSVs; a Django
   │   project scaffolded as the intended control plane, with a working
   │   ingestion app and dashboard shell.
   ↓
v0.4 (current) — Documentation and project hygiene
       Rewriting README/ABOUT/LICENSE to reflect what's actually built,
       and adding a hosted project page — the stage this document
       describes.
```

## 4. Key Features

```
                PROJECT CAPABILITIES

       ┌────────────┐
       │  INGEST    │   yfinance → per-ticker daily OHLCV CSVs      ✅
       └─────┬──────┘
             │
       ┌─────▼──────┐
       │ AGGREGATE  │   Hadoop MapReduce → monthly OHLCV per ticker ✅
       └─────┬──────┘
             │
       ┌─────▼──────┐
       │ TRANSFORM  │   Spark feature engineering (returns, RSI, MACD) 🚧
       └─────┬──────┘
             │
       ┌─────▼──────┐
       │ FORECAST   │   GPU-trained LSTM/Transformer price forecasting 🚧
       └────────────┘
```

- **INGEST** — `app-django/apps/app_ingest/download.py` pulls historical OHLCV data for a set of tickers via `yfinance` and writes it to CSV.
- **AGGREGATE** — the Hadoop job in `app-hadoop/hadoop-finance/` reduces daily rows into one row per ticker-month, with Open/High/Low/Close/Volume computed correctly across the month.
- **TRANSFORM** (planned) — the `etl/` scripts are early drafts of a Spark pipeline that will clean data, engineer features, and build time-series windows.
- **FORECAST** (planned) — `dl/` is reserved for model definitions and training scripts once there's a feature dataset to train on.

## 5. Architecture

Same pipeline as above, with each component's role:

- **Django (control plane)** — not a data processor. Its job is to trigger ingestion and (eventually) the Hadoop/Spark/DL jobs, and present status/results. Keeping it this way means heavy computation never blocks a web request.
- **HDFS (Dockerized)** — the shared storage layer between pipeline stages, running inside a cloned [big-data-europe](https://github.com/big-data-europe) Hadoop cluster (`docker/docker-hadoop/`), rather than a manually-installed local Hadoop.
- **Hadoop MapReduce** — does the one thing MapReduce is genuinely good at here: partition daily records by ticker/month, aggregate independently per partition, combine, and reduce. See "Under the Hood" below for the actual mapper/combiner/reducer logic.
- **Spark (planned)** — takes over once the data is aggregate-shaped, for the DataFrame-style transformations (technical indicators, windowing) that would be awkward to hand-write as MapReduce jobs.
- **GPU training (planned)** — isolated to the one stage that actually benefits from it, keeping every earlier stage runnable on CPU with a bounded memory budget.

## 6. Technology Stack

| Technology         | Role                          | Why                                                                 |
| ------------------ | ------------------------------ | -------------------------------------------------------------------- |
| Java 8 / Maven      | Hadoop job build               | Matches the Hadoop 3.x client libraries' most broadly compatible target |
| Hadoop (MapReduce)  | Batch OHLCV aggregation        | The concrete, hands-on way to learn map/combine/reduce semantics    |
| Docker Compose      | Local Hadoop cluster           | Runs namenode/datanode/resourcemanager without a manual multi-node install |
| Python / Django     | Orchestration control plane    | Familiar web framework to build a job-triggering UI/API on top of   |
| yfinance / pandas   | Data acquisition               | Free, simple access to decades of OHLCV history                     |
| Spark (planned)     | Feature engineering            | DataFrame transformations are far more ergonomic than raw MapReduce for this stage |
| PyTorch/TensorFlow (planned) | Forecasting models   | GPU-accelerated training for LSTM/Transformer architectures         |

## 7. Data Flow (record-level)

```
RAW CSV ROW (date, open, high, low, close, volume)
    │
    ▼
MAPPER — parses the row, extracts ticker symbol from the filename,
         emits key "SYMBOL|YYYY-MM" → OHLCV record
    │
    ▼
COMBINER — partially aggregates OHLCV records sharing a key within
           the same map task, to cut network shuffle volume
    │
    ▼
REDUCER — merges all records for a key: first Open, max High,
          min Low, last Close, summed Volume
    │
    ▼
OUTPUT — one line per ticker-month: "AAPL|2020-01 -> Open=... High=... Low=... Close=... Volume=..."
```

## 8. Project Structure

```
Yahoo-Finance-Full-Historical-Market-Data-Processing/
│
├── app-hadoop/hadoop-finance/   Maven project — the OHLCV MapReduce job (working)
├── app-django/                  Django project — ingestion + dashboard UI
├── docker/                      Cloned big-data-europe Docker images (docker-hadoop in active use)
├── data/                        Raw and processed CSVs
├── etl/                         Early Spark/pandas ETL scripts (not yet wired into a pipeline)
├── spark/                       Spark job entry points (stub)
├── dl/                          Deep learning models/training scripts (empty, planned)
├── config/                      Spark/training config stubs
├── scripts/                     One-off utilities (e.g. parsing Hadoop job output)
├── utils/                       Controller stubs Django will use to trigger jobs
├── site/                        Source for the hosted project page (this file, rendered)
├── README.md                    Quick-reference: what it is, current status, how to run it
├── ABOUT.md                     This file — the detailed project outline
└── LICENSE                      Custom attribution-required license
```

The Hadoop project is kept fully separate from the Django app and the planned Spark/DL code — each is its own standalone concern (Java/Maven vs. Python/Django vs. Python/Spark vs. Python/GPU), sharing data only through HDFS/the filesystem, not through direct code coupling.

## 9. Design Decisions

**Why Hadoop MapReduce for the aggregation stage, instead of just pandas?**
Because the point of this project is learning distributed processing mechanics. Pandas would get the same numbers faster for a handful of tickers, but wouldn't teach anything about partitioning, combiners, or the shuffle phase.

**Why Django as a control plane instead of a processor?**
Keeping Django limited to triggering/monitoring jobs (rather than parsing CSVs or running MapReduce logic inside a view) means a slow or memory-heavy job never blocks a web request, and each layer (Java/Hadoop, Python/Spark, Python/GPU) can be developed and reasoned about independently.

**Why a Dockerized Hadoop cluster instead of installing Hadoop directly?**
A cloned Docker Compose setup gets a realistic multi-container cluster (namenode/datanode/resourcemanager/nodemanager/historyserver) running in minutes, and is far easier to reset to a clean state than a manual local install.

**Why Java 8 for the Hadoop job?**
It's the most broadly compatible target across the Hadoop 3.x client library versions currently declared in `pom.xml`, avoiding version-mismatch issues between the compiled classes and the cluster's runtime.

## 10. Challenges → Solutions

| Challenge                                              | Initial approach                          | Problem                                                 | Solution                                                                 |
| ------------------------------------------------------- | ------------------------------------------ | -------------------------------------------------------- | --------------------------------------------------------------------------- |
| Maven build failed with "No compiler is provided"       | Ran `mvn clean compile` with a JRE on PATH | Maven needs `javac`, which a JRE doesn't include         | Installed a full JDK (Temurin 21) and pointed `JAVA_HOME`/`PATH` at it       |
| `pom.xml` had a duplicate `maven-compiler-plugin` entry  | Left both declarations in place            | Maven warned the effective build model was malformed     | Kept a single compiler-plugin declaration                                   |
| Mixed Hadoop client versions (3.1.1 and 3.3.6) as deps   | Declared both incidentally                 | Ambiguous effective dependency versions, build warnings  | Documented as a known cleanup item (see Roadmap) rather than silently ignored |

## 11. Performance

Not yet measured. The current pipeline runs against a small number of tickers on a single-node Dockerized cluster; no throughput or timing benchmarks have been collected. This is planned once the pipeline runs against a larger ticker universe.

## 12. Security

```
Security considerations

✓ No secrets or credentials committed to the repository
✗ No authentication/authorization layer (not applicable yet — no exposed service)
✗ No input validation on ingested CSVs beyond basic parsing (future work)
✗ No dependency vulnerability scanning configured yet (future work)
```

This is a local, single-user learning project with no exposed network service today, so most of the checklist is genuinely not yet applicable rather than a gap in something that's live.

## 13. Testing

**No automated tests exist yet**, in either the Hadoop project (`app-hadoop/hadoop-finance/` has no `src/test/java` and no JUnit dependency) or the Django project (each app has only the unmodified 3-line Django test stub). This is a real, acknowledged gap — see the Roadmap.

## 14. Under the Hood

The one part of this project with real internals worth walking through is the MapReduce job itself:

```
                UNDER THE HOOD: OHLCVDriver

Input Split (one CSV file)
     │
     ▼
OHLCVMapper
     │  parses each line, reads ticker symbol from the input
     │  file's name, emits (symbol|month) → OHLCVWritable
     ▼
OHLCVCombiner  (runs per map task, before shuffle)
     │  partially aggregates records sharing a key,
     │  cutting how much data crosses the network
     ▼
      shuffle & sort (Hadoop-managed)
     │
     ▼
OHLCVReducer
     │  merges all records for a symbol|month key into
     │  one final OHLCV summary
     ▼
Output (HDFS)
```

## 15. Engineering Questions

```
What happens if a CSV row is malformed (missing/extra columns)?
  → Not handled yet — the mapper assumes well-formed rows. Planned: skip
    and log malformed rows instead of failing the task.

What happens if the Docker cluster restarts mid-job?
  → Not tested. Hadoop's own task-retry mechanism would likely apply,
    but this hasn't been verified against this specific setup.

What happens if two tickers have overlapping date ranges but different
column orders?
  → Not handled — the mapper assumes a fixed Yahoo Finance CSV column
    order (Date, Open, High, Low, Close, Volume).
```

These are left as open questions rather than answered with an implementation that doesn't exist yet.

## 16. What I Learned

```
### Key Learnings

• How Hadoop's map → combine → shuffle → reduce phases actually fit together,
  by writing a real Combiner rather than just a Mapper/Reducer pair
• Why Maven needs a JDK (not just a JRE) on PATH to compile anything
• How to structure a multi-language project (Java/Hadoop, Python/Django) so
  each part stays independently buildable
• The value of writing a custom Hadoop Writable (OHLCVWritable) to carry a
  structured record between map and reduce, instead of serializing to text
```

## 17. Roadmap

```
                    ROADMAP

         ┌───────────────┐
         │      v0.4     │
         │  (current)    │
         │  Docs + Pages │
         └───────┬───────┘
                 ↓
         ┌───────────────┐
         │      v0.5     │
         │  Validate &   │
         │  partition    │
         │  MapReduce    │
         │  output       │
         └───────┬───────┘
                 ↓
         ┌───────────────┐
         │      v0.7     │
         │  Spark feature│
         │  pipeline     │
         └───────┬───────┘
                 ↓
         ┌───────────────┐
         │      v1.0     │
         │  Django-driven│
         │  orchestration│
         └───────┬───────┘
                 ↓
         ┌───────────────┐
         │      v1.5     │
         │  First GPU    │
         │  LSTM model   │
         └───────────────┘
```

## 18. What This Project Demonstrates

```
This project demonstrates:

✓ Hadoop MapReduce (Mapper, Combiner, Reducer, custom Writable)
✓ Maven/Java build tooling
✓ Docker Compose for local distributed-systems development
✓ Separating orchestration (Django) from processing (Hadoop/Spark)
✓ Honest project documentation — distinguishing what's built from what's planned
```

## Credits / License

**Author:** Aashish Bansal — [github.com/aashish22bansal](https://github.com/aashish22bansal)

Licensed under a custom Attribution-Required License — see [LICENSE](LICENSE). Any use, modification, or deployment of this project, including as a hosted service, must give clear, visible credit to the original author and link back to this repository.
