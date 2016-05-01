package com.pgaray.stockmarketviewer;

/**
 * Created by Pablo on 4/30/2016.
 */
public final class stockDetailsEntry {

    private final String title;
    private final String value;
    private final int icon;

    public stockDetailsEntry(final String title, final String value,
                     final int icon) {
        this.title = title;
        this.value = value;
        this.icon = icon;
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
    public int getIcon() {
        return icon;
    }

}
