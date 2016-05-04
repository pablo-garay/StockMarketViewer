package com.pgaray.stockmarketviewer;

/**
 * Created by Pablo on 4/30/2016.
 */
public final class NewsEntry {

    private final String title;
    private final String content;
    private final String publisher;
    private final String date;
    private final String url;

    public NewsEntry(final String title, final String content,
                     final String publisher, final String date, final String url) {
        this.title = title;
        this.content = content;
        this.publisher = publisher;
        this.date = date;
        this.url = url;
    }
    /**
     * @return Title of news entry
     */
    public String getNewsTitle() {
        return title;
    }

    /**
     * @return Content of news entry
     */
    public String getNewsContent() { return content; }

    /**
     * @return Publisher of news entry
     */
    public String getNewsPublisher() { return publisher; }

    /**
     * @return Date of news entry
     */
    public String getNewsDate() { return date; }

    /**
     * @return Url of news entry
     */
    public String getNewsUrl() { return url; }
}
