package cn.edu.iip.nju.model.vo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
@Data
@ToString
public class ProductNegListForm {
    private String productName;
    private String province;
    private String injureDegree;
    private String injureType;
    private Date datefrom;
    private Date dateto = new Date();

}
