from django.shortcuts import render

# Create your views here.
from django.shortcuts import render
from apps.app_ingest.services.data_loader import load_monthly_data
from .plotting import build_ohlc_vwap_chart

def dashboard(request):
    df = load_monthly_data()

    # Default ticker
    ticker = request.GET.get("ticker", df["ticker"].iloc[0])

    df_ticker = df[df["ticker"] == ticker.upper()]

    chart_html = build_ohlc_vwap_chart(df_ticker, ticker.upper())

    context = {
        "chart": chart_html,
        "tickers": sorted(df["ticker"].unique()),
        "selected_ticker": ticker.upper()
    }

    return render(request, "app_ui/dashboard.html", context)
