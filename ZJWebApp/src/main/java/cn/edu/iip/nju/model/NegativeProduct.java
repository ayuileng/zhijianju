package cn.edu.iip.nju.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by xu on 2017/10/26.
 * 负面产品 从伤害事件案例库中检索生成，不需要存库
 */
@Data
public class NegativeProduct implements Serializable{

    private String productName;     //产品名称

    private String injureType;      //受伤类型

    private String injureDegree;    //伤害程度

    private String province;        //伤害发生省份

    private Date injureDate;        //伤害发生时间

    private String newsUrl;





}
