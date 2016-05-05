package com.pgaray.stockmarketviewer;

/**
 * Created by Pablo on 4/30/2016.
 */
public final class FavoriteEntry {

    private final String symbol;
    private final String name;
    private final String stockValue;
    private final String changePercent;
    private final String marketCap;

    public FavoriteEntry(final String symbol, final String name,
                         final String stockValue, final String changePercent, final String marketCap) {
        this.symbol = symbol;
        this.name = name;
        this.stockValue = stockValue;
        this.changePercent = changePercent;
        this.marketCap = marketCap;
    }
    /**
     * @return symbol of favorites entry
     */
    public String getFavoriteSymbol() { return symbol; }

    /**
     * @return name of favorites entry
     */
    public String getFavoriteName() { return name; }

    /**
     * @return stockValue of favorites entry
     */
    public String getFavoriteStockValue() { return stockValue; }

    /**
     * @return change of favorites entry
     */
    public String getFavoriteChange() { return changePercent; }

    /**
     * @return change of favorites entry
     */
    public String getFavoriteMarketCap() { return marketCap; }
}
