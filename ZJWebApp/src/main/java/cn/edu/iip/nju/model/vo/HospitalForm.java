package cn.edu.iip.nju.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * Created by xu on 2017/11/15.
 */
@Data
public class HospitalForm {
    private Date datefrom;
    private Date dateto = new Date();
    private String productType;
    private String injureDegree;
    private String howgetInjure;
    private int page = 1;

    @Override
    public String toString() {
        return "HospitalForm{" +
                "datefrom='" + datefrom + '\'' +
                ", dateto='" + dateto + '\'' +
                ", productType='" + productType + '\'' +
                ", injureDegree='" + injureDegree + '\'' +
                ", howgetInjure='" + howgetInjure + '\'' +
                ", page=" + page +
                '}';
    }
}
