from django.shortcuts import render

# Create your views here.
from django.http import JsonResponse
from .services.data_loader import load_monthly_data

def list_tickers(request):
    df = load_monthly_data()
    return JsonResponse(
        {"tickers": sorted(df["ticker"].unique().tolist())}
    )

def monthly_data(request, ticker):
    df = load_monthly_data()
    data = df[df["ticker"] == ticker.upper()]
    return JsonResponse(data.to_dict(orient="records"), safe=False)
