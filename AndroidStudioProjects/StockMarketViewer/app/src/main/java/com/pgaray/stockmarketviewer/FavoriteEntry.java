package com.pgaray.stockmarketviewer;

/**
 * Created by Pablo on 4/30/2016.
 */
public final class FavoriteEntry {

    private String symbol;
    private String name;
    private String stockValue;
    private String changePercent;
    private String marketCap;
    private int changeIndicator;

    public FavoriteEntry(final String symbol, final String name,
                         final String stockValue, final String changePercent,
                         final int changeIndicator, final String marketCap) {
        this.symbol = symbol;
        this.name = name;
        this.stockValue = stockValue;
        this.changePercent = changePercent;
        this.marketCap = marketCap;
        this.changeIndicator = changeIndicator;
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
    public int getFavoriteChangeIndicator() { return changeIndicator; }

    /**
     * @return change of favorites entry
     */
    public String getFavoriteMarketCap() { return marketCap; }

    /* SETTER */
    public void setFavoriteEntry(final String symbol, final String name,final String stockValue,
                                 final String changePercent, final int changeIndicator,
                                 final String marketCap){
        this.symbol = symbol;
        this.name = name;
        this.stockValue = stockValue;
        this.changePercent = changePercent;
        this.changeIndicator = changeIndicator;
        this.marketCap = marketCap;
    }
}
