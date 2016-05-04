package com.pgaray.stockmarketviewer;

/**
 * Created by Pablo on 4/30/2016.
 */
public final class StockDetailsEntry {

    private final String title;
    private final String value;
    private final int indicatorValue;

    public StockDetailsEntry(final String title, final String value,
                             final int indicatorValue) {
        this.title = title;
        this.value = value;
        this.indicatorValue = indicatorValue;
    }

    /**
     * @return Title of stock details entry
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return Value of stock details entry
     */
    public String getValue() {
        return value;
    }

    /**
     * @return Icon of this stock details entry
     */
    public int getIndicatorValue() {
        return indicatorValue;
    }

}
