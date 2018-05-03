package cn.edu.iip.nju.model.form;

/**
 * Created by xu on 2017/11/15.
 */
public class HospitalForm {
    private String datefrom;
    private String dateto;
    private String productType;
    private String injureDegree;
    private String ifIntention;
    private int page = 1;

    public String getDatefrom() {
        return datefrom;
    }

    public void setDatefrom(String datefrom) {
        this.datefrom = datefrom;
    }

    public String getDateto() {
        return dateto;
    }

    public void setDateto(String dateto) {
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

    public String getIfIntention() {
        return ifIntention;
    }

    public void setIfIntention(String ifIntention) {
        this.ifIntention = ifIntention;
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
                ", ifIntention='" + ifIntention + '\'' +
                ", page=" + page +
                '}';
    }
}
