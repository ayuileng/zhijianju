package cn.edu.iip.nju.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by FHH&&WSS
 */

@Entity(name = "web_data")
public class WebData implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String title;
    @Lob
    @Column(name = "content",columnDefinition="LONGTEXT")
    private String content;
    private String url;
    @Lob
    @Column(name = "html",columnDefinition="LONGTEXT")
    private String html;
    @Column(name = "post_time")
    private Date postTime;
    private Date crawlerTime;
    private String sourceName;
    //下面4个属性就是标签


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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
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

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public String toString() {
        return "WebData{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                ", html='" + html + '\'' +
                ", postTime=" + postTime +
                ", crawlerTime=" + crawlerTime +
                ", sourceName='" + sourceName + '\'' +
                '}';
    }
}
