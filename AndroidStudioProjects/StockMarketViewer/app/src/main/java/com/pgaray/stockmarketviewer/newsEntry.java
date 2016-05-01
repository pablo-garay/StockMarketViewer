package com.pgaray.stockmarketviewer;

/**
 * Created by Pablo on 4/30/2016.
 */
public final class NewsEntry {

    private final String title;
    private final String content;
    private final String publisher;
    private final String date;

    public NewsEntry(final String title, final String content,
                     final String publisher, final String date) {
        this.title = title;
        this.content = content;
        this.publisher = publisher;
        this.date = date;
    }
    /**
     * @return Title of news entry
     */
    public String getNewsTitle() {
        return title;
    }

    /**
     * @return Value of news entry
     */
    public String getNewsContent() { return content; }

    /**
     * @return Value of news entry
     */
    public String getNewsPublisher() { return publisher; }

    /**
     * @return Value of news entry
     */
    public String getNewsDate() { return date; }
}
