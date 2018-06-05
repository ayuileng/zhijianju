package cn.edu.iip.nju.model;

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.Date;

/**
 * 封装了solr的查询结果
 * Created by xu on 2017/9/7.
 */
@SolrDocument(solrCoreName = "mycore")
@Data
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



}
