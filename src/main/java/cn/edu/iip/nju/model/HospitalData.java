package cn.edu.iip.nju.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by xu on 2017/10/23.
 */
@Entity
public class HospitalData implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String gender;
    private Integer age;
    private String huji;
    private String eduDegree;
    private String vocation;
    @DateTimeFormat(style = "yyyy-MM-dd")
    private Date injureDate;//伤害发生时间
    @DateTimeFormat(style = "yyyy-MM-dd")
    private Date visDate;//就诊时间
    private String injureReason;//伤害发生原因
    private String injureLocation;//伤害发生地点
    private String activityWhenInjure;//伤害发生时活动
    private String ifIntentional;//是否故意
    private String injureType;//上伤害性质
    private String injureSite;//伤害部位
    private String injureDegree;//伤害严重程度
    private String injurejudge;//伤害临床诊断
    private String injureResult;//伤害结局
    private String product;//伤害涉及物品
    private String alcohol;//饮酒状况
    private String injureSystem;//伤害累及系统
    private String howGetInjure;//产品质量是否有关

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getHuji() {
        return huji;
    }

    public void setHuji(String huji) {
        this.huji = huji;
    }

    public String getEduDegree() {
        return eduDegree;
    }

    public void setEduDegree(String eduDegree) {
        this.eduDegree = eduDegree;
    }

    public String getVocation() {
        return vocation;
    }

    public void setVocation(String vocation) {
        this.vocation = vocation;
    }

    public Date getInjureDate() {
        return injureDate;
    }

    public void setInjureDate(Date injureDate) {
        this.injureDate = injureDate;
    }

    public Date getVisDate() {
        return visDate;
    }

    public void setVisDate(Date visDate) {
        this.visDate = visDate;
    }

    public String getInjureReason() {
        return injureReason;
    }

    public void setInjureReason(String injureReason) {
        this.injureReason = injureReason;
    }

    public String getInjureLocation() {
        return injureLocation;
    }

    public void setInjureLocation(String injureLocation) {
        this.injureLocation = injureLocation;
    }

    public String getActivityWhenInjure() {
        return activityWhenInjure;
    }

    public void setActivityWhenInjure(String activityWhenInjure) {
        this.activityWhenInjure = activityWhenInjure;
    }

    public String getIfIntentional() {
        return ifIntentional;
    }

    public void setIfIntentional(String ifIntentional) {
        this.ifIntentional = ifIntentional;
    }

    public String getInjureType() {
        return injureType;
    }

    public void setInjureType(String injureType) {
        this.injureType = injureType;
    }

    public String getInjureSite() {
        return injureSite;
    }

    public void setInjureSite(String injureSite) {
        this.injureSite = injureSite;
    }

    public String getInjureDegree() {
        return injureDegree;
    }

    public void setInjureDegree(String injureDegree) {
        this.injureDegree = injureDegree;
    }

    public String getInjurejudge() {
        return injurejudge;
    }

    public void setInjurejudge(String injurejudge) {
        this.injurejudge = injurejudge;
    }

    public String getInjureResult() {
        return injureResult;
    }

    public void setInjureResult(String injureResult) {
        this.injureResult = injureResult;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(String alcohol) {
        this.alcohol = alcohol;
    }

    public String getInjureSystem() {
        return injureSystem;
    }

    public void setInjureSystem(String injureSystem) {
        this.injureSystem = injureSystem;
    }

    public String getHowGetInjure() {
        return howGetInjure;
    }

    public void setHowGetInjure(String howGetInjure) {
        this.howGetInjure = howGetInjure;
    }

    @Override
    public String toString() {
        return "HospitalData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", huji='" + huji + '\'' +
                ", eduDegree='" + eduDegree + '\'' +
                ", vocation='" + vocation + '\'' +
                ", injureDate=" + injureDate +
                ", visDate=" + visDate +
                ", injureReason='" + injureReason + '\'' +
                ", injureLocation='" + injureLocation + '\'' +
                ", activityWhenInjure='" + activityWhenInjure + '\'' +
                ", ifIntentional='" + ifIntentional + '\'' +
                ", injureType='" + injureType + '\'' +
                ", injureSite='" + injureSite + '\'' +
                ", injureDegree='" + injureDegree + '\'' +
                ", injurejudge='" + injurejudge + '\'' +
                ", injureResult='" + injureResult + '\'' +
                ", product='" + product + '\'' +
                ", alcohol='" + alcohol + '\'' +
                ", injureSystem='" + injureSystem + '\'' +
                ", howGetInjure='" + howGetInjure + '\'' +
                '}';
    }
}
