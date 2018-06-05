package cn.edu.iip.nju.model.vo;

import java.util.Date;

/**
 * Created by xu on 2017/11/15.
 */
public class HospitalForm {
    private Date datefrom;
    private Date dateto = new Date();
    private String productType;
    private String injureDegree;
    private String howgetInjure;
    private int page = 1;

    public Date getDatefrom() {
        return datefrom;
    }

    public void setDatefrom(Date datefrom) {
        this.datefrom = datefrom;
    }

    public Date getDateto() {
        return dateto;
    }

    public void setDateto(Date dateto) {
        this.dateto = dateto;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getInjureDegree() {
        return injureDegree;
    }

    public void setInjureDegree(String injureDegree) {
        this.injureDegree = injureDegree;
    }

    public String getHowgetInjure() {
        return howgetInjure;
    }

    public void setHowgetInjure(String howgetInjure) {
        this.howgetInjure = howgetInjure;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

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
