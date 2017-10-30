package cn.edu.iip.nju.model;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.Date;

/**
 * 封装了solr的查询结果
 * Created by xu on 2017/9/7.
 */
@SolrDocument(solrCoreName = "webdatacore")
public class Document {
    @Id
    @Indexed("uuid")
    private String id;
    @Indexed
    private String title;
    @Indexed
    private String content;
    @Indexed
    private Date postTime;
    @Indexed
    private String html;
    @Indexed
    private String url;
    @Indexed
    private String source;
    @Field("search_text")
    private String searchText;
    public String  getId() {
        return id;
    }

    public void setId(String  id) {
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

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getPostTime() {
        return postTime;
    }

    public void setPostTime(Date postTime) {
        this.postTime = postTime;
    }


}
