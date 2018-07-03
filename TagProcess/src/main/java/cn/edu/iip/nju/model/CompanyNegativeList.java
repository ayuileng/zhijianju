package cn.edu.iip.nju.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by xu on 2017/10/26.
 * 企业负面信息
 */
@Entity
@Table(indexes = {@Index(name = "com_neg_pro_inx",columnList ="province")})
public class CompanyNegativeList implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String companyName = "";     //企业名称

    private Integer caseNum = 0;        //案例数

    private String injureDegree = "";    //伤害程度

    private Integer callbackNum = 0;    //召回次数

    private Double passPercent = 1.0;     //抽查合格率

    private String province = "";        //企业所在省份

    private Integer passCase;
    private Integer unPassCase;

    public Integer getPassCase() {
        return passCase;
    }

    public void setPassCase(Integer passCase) {
        this.passCase = passCase;
    }

    public Integer getUnPassCase() {
        return unPassCase;
    }

    public void setUnPassCase(Integer unPassCase) {
        this.unPassCase = unPassCase;
    }

    public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

}
