package cn.edu.iip.nju.model;

import lombok.Data;
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
@Data
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
    private String productCat;

}
