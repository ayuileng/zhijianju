package cn.edu.iip.nju.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by xu on 2017/9/21.
 * 标签库
 */
@Entity
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer documentId;
    private String productName;
    private String productType;
    private String area;
    private String injureType;
    private String injureCount;
    private String brand;//商标
    private String size;//型号
    private Boolean isPass;//是否合格
    private String company;
    private String label1;//未定
    private String label2;
    private String label3;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Integer documentId) {
        this.documentId = documentId;
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getInjureType() {
        return injureType;
    }

    public void setInjureType(String injureType) {
        this.injureType = injureType;
    }

    public String getInjureCount() {
        return injureCount;
    }

    public void setInjureCount(String injureCount) {
        this.injureCount = injureCount;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Boolean getPass() {
        return isPass;
    }

    public void setPass(Boolean pass) {
        isPass = pass;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLabel1() {
        return label1;
    }

    public void setLabel1(String label1) {
        this.label1 = label1;
    }

    public String getLabel2() {
        return label2;
    }

    public void setLabel2(String label2) {
        this.label2 = label2;
    }

    public String getLabel3() {
        return label3;
    }

    public void setLabel3(String label3) {
        this.label3 = label3;
    }

    @Override
    public String toString() {
        return "Label{" +
                "id=" + id +
                ", documentId=" + documentId +
                ", productName='" + productName + '\'' +
                ", productType='" + productType + '\'' +
                ", area='" + area + '\'' +
                ", injureType='" + injureType + '\'' +
                ", injureCount='" + injureCount + '\'' +
                ", brand='" + brand + '\'' +
                ", size='" + size + '\'' +
                ", isPass=" + isPass +
                ", company='" + company + '\'' +
                ", label1='" + label1 + '\'' +
                ", label2='" + label2 + '\'' +
                ", label3='" + label3 + '\'' +
                '}';
    }
}
