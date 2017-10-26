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
    private String productType;//产品类别
    private String brand;//商标
    private Date injureTime;//伤害发生时间
    private String injureArea;//伤害发生地点
    private String injureType;//伤害类别
    private String injureDegree;//伤害严重程度
    private Integer injureIndex;//伤害指标

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

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
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

    public String getInjureDegree() {
        return injureDegree;
    }

    public void setInjureDegree(String injureDegree) {
        this.injureDegree = injureDegree;
    }

    public Integer getInjureIndex() {
        return injureIndex;
    }

    public void setInjureIndex(Integer injureIndex) {
        this.injureIndex = injureIndex;
    }

    public InjureCase(String productName, String productType, String brand, Date injureTime, String injureArea, String injureType, String injureDegree, Integer injureIndex) {
        this.productName = productName;
        this.productType = productType;
        this.brand = brand;
        this.injureTime = injureTime;
        this.injureArea = injureArea;
        this.injureType = injureType;
        this.injureDegree = injureDegree;
        this.injureIndex = injureIndex;
    }

    public InjureCase() {
    }

    @Override
    public String toString() {
        return "InjureCase{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", productType='" + productType + '\'' +
                ", brand='" + brand + '\'' +
                ", injureTime=" + injureTime +
                ", injureArea='" + injureArea + '\'' +
                ", injureType='" + injureType + '\'' +
                ", injureDegree='" + injureDegree + '\'' +
                ", injureIndex=" + injureIndex +
                '}';
    }
}
