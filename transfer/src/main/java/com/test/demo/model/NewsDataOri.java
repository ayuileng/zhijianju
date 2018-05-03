package com.test.demo.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by xu on 2017/11/29.
 */
@Entity
public class NewsDataOri {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    @Lob
    @Column(name = "content",columnDefinition="LONGTEXT")
    private String content;
    @Column(name = "post_time")
    private Date postTime;
    @Column(name = "crawler_time")
    private Date crawlerTime;
    @Column(name = "url",length = 1024)
    private String url;
    private Boolean isInjureNews;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPostTime() {
        return postTime;
    }

    public void setPostTime(Date postTime) {
        this.postTime = postTime;
    }

    public Date getCrawlerTime() {
        return crawlerTime;
    }

    public void setCrawlerTime(Date crawlerTime) {
        this.crawlerTime = crawlerTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getInjureNews() {
        return isInjureNews;
    }

    public void setInjureNews(Boolean injureNews) {
        isInjureNews = injureNews;
    }
}
