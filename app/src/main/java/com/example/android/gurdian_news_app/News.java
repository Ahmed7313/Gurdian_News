package com.example.android.gurdian_news_app;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class News {

    private String sectionName;
    private String webPublicationDate;
    private String webTitle;
    private String webUrl;
    private String section;
    private String imageUrl;
    private String authorName;

    public News(String sectionName, String imageUrl, String webPublicationDate, String webTitle, String webUrl, String section, String authorName) {
        this.sectionName = sectionName;
        this.webPublicationDate = webPublicationDate;
        this.webTitle = webTitle;
        this.webUrl = webUrl;
        this.section = section;
        this.authorName = authorName;
        this.imageUrl = imageUrl;
    }

    public String getSection() {
        return section;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSectionName() {
        return sectionName;
    }

    private Date fromISO8601UTC(String dateStr) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);

        try {
            return df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getFormatedDate() {
        Date date = fromISO8601UTC(webPublicationDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM. yyyy");
        return dateFormat.format(date);
    }


    public String getWebTitle() {
        return webTitle;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getAuthorName() {
        return authorName;
    }
}
