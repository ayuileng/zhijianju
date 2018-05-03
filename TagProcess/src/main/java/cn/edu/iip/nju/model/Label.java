package cn.edu.iip.nju.model;

import javax.persistence.*;

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
    @Lob
    @Column(name = "product_name", columnDefinition = "LONGTEXT")
    private String productName;
    private String area;
    private String injureType;
    private String injureCount;
    private Boolean isPass;//是否合格
    private String company;
    private String posttime;//post_time
    private String zhaohui;//是否召回
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

    public String getPosttime() {
        return posttime;
    }

    public void setPosttime(String posttime) {
        this.posttime = posttime;
    }

    public String getZhaohui() {
        return zhaohui;
    }

    public void setZhaohui(String zhaohui) {
        this.zhaohui = zhaohui;
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
                ", area='" + area + '\'' +
                ", injureType='" + injureType + '\'' +
                ", injureCount='" + injureCount + '\'' +
                ", isPass=" + isPass +
                ", company='" + company + '\'' +
                ", posttime='" + posttime + '\'' +
                ", zhaohui='" + zhaohui + '\'' +
                ", label3='" + label3 + '\'' +
                '}';
    }
}
