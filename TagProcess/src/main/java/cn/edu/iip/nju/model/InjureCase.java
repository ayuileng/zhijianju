package cn.edu.iip.nju.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by xu on 2017/10/24.
 * 伤害案例库
 * 使用了jpa建立索引
 */
@Entity
@Table(indexes = {@Index(name = "injure_case_product_name_index",columnList ="productName"),
        @Index(name = "injure_case_injure_type_index",columnList ="injureType"),
        @Index(name = "injure_case_injure_degree_index",columnList ="injureDegree"),
        @Index(name = "injure_case_prov_index",columnList ="province")})
public class InjureCase implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String productName;//产品名

    private Date injureTime;//伤害发生时间
    private String injureArea = "";//伤害发生地点
    private String injureType = "";//伤害类别
    private String province = "";
    private String injureDegree = "";

    public String getInjureDegree() {
        return injureDegree;
    }

    public void setInjureDegree(String injureDegree) {
        this.injureDegree = injureDegree;
    }
    @Column(name = "url",unique = true)
    private String url;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

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



    public Date getInjureTime() {
        return injureTime;
    }

    public void setInjureTime(Date injureTime) {
        this.injureTime = injureTime;
    }

    public String getInjureArea() {
        return injureArea;
    }

    public void setInjureArea(String injureArea) {
        this.injureArea = injureArea;
    }

    public String getInjureType() {
        return injureType;
    }

    public void setInjureType(String injureType) {
        this.injureType = injureType;
    }



    @Override
    public String toString() {
        return "InjureCase{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", injureTime=" + injureTime +
                ", injureArea='" + injureArea + '\'' +
                ", injureType='" + injureType + '\'' +
                ", province='" + province + '\'' +
                ", injureDegree='" + injureDegree + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
