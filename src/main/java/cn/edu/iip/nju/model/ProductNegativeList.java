package cn.edu.iip.nju.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by xu on 2017/10/26.
 * 负面产品
 */
@Entity
public class ProductNegativeList implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String productName;     //产品名称

    private Integer caseNum;        //案例数

    private String injureDegree;    //伤害程度

    private Integer callbackNum;    //召回次数

    private Double passPercent;     //抽查合格率

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

    public Integer getCaseNum() {
        return caseNum;
    }

    public void setCaseNum(Integer caseNum) {
        this.caseNum = caseNum;
    }

    public String getInjureDegree() {
        return injureDegree;
    }

    public void setInjureDegree(String injureDegree) {
        this.injureDegree = injureDegree;
    }

    public Integer getCallbackNum() {
        return callbackNum;
    }

    public void setCallbackNum(Integer callbackNum) {
        this.callbackNum = callbackNum;
    }

    public Double getPassPercent() {
        return passPercent;
    }

    public void setPassPercent(Double passPercent) {
        this.passPercent = passPercent;
    }

    public ProductNegativeList() {
    }

    public ProductNegativeList(String productName, Integer caseNum, String injureDegree, Integer callbackNum, Double passPercent) {
        this.productName = productName;
        this.caseNum = caseNum;
        this.injureDegree = injureDegree;
        this.callbackNum = callbackNum;
        this.passPercent = passPercent;
    }

    @Override
    public String toString() {
        return "ProductNegativeList{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", caseNum=" + caseNum +
                ", injureDegree='" + injureDegree + '\'' +
                ", callbackNum=" + callbackNum +
                ", passPercent=" + passPercent +
                '}';
    }
}
