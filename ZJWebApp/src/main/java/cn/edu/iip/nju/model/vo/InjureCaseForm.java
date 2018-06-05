package cn.edu.iip.nju.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * Created by xu on 2017/11/8.
 * 伤害事件案例库页面的form对象
 */
@Data
public class InjureCaseForm {
    private String productName;
    private String injureType;
    private String area;
    private String province;
    private Date datefrom;
    private Date dateto = new Date();
    private String injureDegree;
    private int sort=2;
    private int page=1;

}
