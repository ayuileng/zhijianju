package cn.edu.iip.nju.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by xu on 2017/11/29.
 */
@Entity
@Data
public class NewsData {
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


}
