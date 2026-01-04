import plotly.graph_objects as go

def build_ohlc_vwap_chart(df, ticker):
    fig = go.Figure()

    # Candlestick
    fig.add_trace(go.Candlestick(
        x=df["month"],
        open=df["open"],
        high=df["high"],
        low=df["low"],
        close=df["close"],
        name="OHLC"
    ))

    # VWAP Line
    fig.add_trace(go.Scatter(
        x=df["month"],
        y=df["vwap"],
        mode="lines",
        name="VWAP",
        line=dict(width=2)
    ))

    fig.update_layout(
        title=f"{ticker} – Monthly OHLC & VWAP",
        xaxis_title="Month",
        yaxis_title="Price",
        template="plotly_white",
        height=600,
        xaxis_rangeslider_visible=False
    )

    return fig.to_html(full_html=False)
