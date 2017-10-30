package cn.edu.iip.nju.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by xu on 2017/10/26.
 * 附件数据表
 */
@Entity
public class AttachmentData implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //    产品名称
    private String name;
    //    商标
    private String shangbiao;
    //    规格型号
    private String guigexinghao;
    //    生产日期、批号
    private String birthday;
    //    企业名
    private String factoryName;
    //    企业所在地
    private String factoryAddress;
    //    检验结果
    private String result;
    //    不合格原因
    private String errorReason;
    //    承建机构
    private String chengjianjigou;

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

    public String getShangbiao() {
        return shangbiao;
    }

    public void setShangbiao(String shangbiao) {
        this.shangbiao = shangbiao;
    }
    //TODO 这个字段会出现Data truncation: Data too long for column 'guigexinghao' at row 1
    @Lob
    @Column(name = "guigexinghao", columnDefinition = "LONGTEXT")
    public String getGuigexinghao() {
        return guigexinghao;
    }

    public void setGuigexinghao(String guigexinghao) {
        this.guigexinghao = guigexinghao;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getFactoryAddress() {
        return factoryAddress;
    }

    public void setFactoryAddress(String factoryAddress) {
        this.factoryAddress = factoryAddress;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public String getChengjianjigou() {
        return chengjianjigou;
    }

    public void setChengjianjigou(String chengjianjigou) {
        this.chengjianjigou = chengjianjigou;
    }

    public AttachmentData() {
    }

    public AttachmentData(String name, String shangbiao,
                          String guigexinghao, String birthday,
                          String factoryName, String factoryAddress,
                          String result, String errorReason,
                          String chengjianjigou) {
        this.name = name;
        this.shangbiao = shangbiao;
        this.guigexinghao = guigexinghao;
        this.birthday = birthday;
        this.factoryName = factoryName;
        this.factoryAddress = factoryAddress;
        this.result = result;
        this.errorReason = errorReason;
        this.chengjianjigou = chengjianjigou;
    }

    @Override
    public String toString() {
        return "AttachmentData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", shangbiao='" + shangbiao + '\'' +
                ", guigexinghao='" + guigexinghao + '\'' +
                ", birthday='" + birthday + '\'' +
                ", factoryName='" + factoryName + '\'' +
                ", factoryAddress='" + factoryAddress + '\'' +
                ", result='" + result + '\'' +
                ", errorReason='" + errorReason + '\'' +
                ", chengjianjigou='" + chengjianjigou + '\'' +
                '}';
    }


    public boolean isFilled(){
        int i=0;
        if(getErrorReason() == null || getErrorReason().isEmpty()){
            i++;
        }
        if(getBirthday() == null || getBirthday().isEmpty()){
            i++;
        }
        if(getChengjianjigou() == null || getChengjianjigou().isEmpty()){
            i++;
        }
        if(getFactoryAddress() == null || getFactoryAddress().isEmpty()){
            i++;
        }
        if(getFactoryName() == null || getFactoryName().isEmpty()){
            i++;
        }
        if(getGuigexinghao() == null || getGuigexinghao().isEmpty()){
            i++;
        }
        if(getName() == null || getName().isEmpty()){
            i++;
        }
        if(getResult() == null || getResult().isEmpty()){
            i++;
        }
        if(getShangbiao() == null || getShangbiao().isEmpty()){
            i++;
        }
        if(i>=5){
            return false;
        }else {
            return true;
        }
    }
}
