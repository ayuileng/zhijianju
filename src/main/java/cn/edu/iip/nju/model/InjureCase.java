package cn.edu.iip.nju.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by xu on 2017/10/24.
 * 伤害案例库
 */
@Entity
public class InjureCase implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String productName;//产品名

    private Date injureTime;//伤害发生时间
    private String injureArea;//伤害发生地点
    private String injureType;//伤害类别
    private String province;
    private Integer injureIndex;//伤害指标
    private String injureDegree;

    public String getInjureDegree() {
        return injureDegree;
    }

    public void setInjureDegree(String injureDegree) {
        this.injureDegree = injureDegree;
    }

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


    public Integer getInjureIndex() {
        return injureIndex;
    }

    public void setInjureIndex(Integer injureIndex) {
        this.injureIndex = injureIndex;
    }

}
