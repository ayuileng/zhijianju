package cn.edu.iip.nju.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by xu on 2017/11/18.
 */
@Entity
public class JingDong implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String productName;
    @Lob
    @Column(name = "comment",columnDefinition="LONGTEXT")
    private String comment;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
