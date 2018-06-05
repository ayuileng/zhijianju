package cn.edu.iip.nju.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by FHH&&WSS
 */

@Entity(name = "web_data")
@Data
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
    private Date crawlTime;
    private String sourceName;
    //下面4个属性就是标签



}
