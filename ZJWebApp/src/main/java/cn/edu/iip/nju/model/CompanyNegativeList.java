package cn.edu.iip.nju.model;

import lombok.Data;

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
@Data
public class CompanyNegativeList implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String companyName;     //企业名称

    private Integer caseNum;        //案例数

    private String injureDegree;    //伤害程度

    private Integer callbackNum;    //召回次数

    private Double passPercent;     //抽查合格率

    private String province;        //企业所在省份


}
