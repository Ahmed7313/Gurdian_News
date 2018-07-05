package com.example.android.gurdian_news_app;

public class News {

    private String sectionName;
    private String webPublicationDate;
    private String webTitle;
    private String webUrl;
    private String section;
    private String authorName;

    public News (String sectionName, String webPublicationDate, String webTitle , String webUrl, String section , String authorName){
        this.sectionName = sectionName;
        this.webPublicationDate = webPublicationDate;
        this.webTitle = webTitle;
        this.webUrl = webUrl;
        this.section = section;
        this.authorName = authorName;
    }

    public String getSection() {
        return section;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getWebPublicationDate() {

        return webPublicationDate;
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
