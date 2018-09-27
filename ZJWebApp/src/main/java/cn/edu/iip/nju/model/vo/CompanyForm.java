package cn.edu.iip.nju.model.vo;

import lombok.Data;

@Data
public class CompanyForm {
    private String province;
    private String factory;
    private int sort=0;
    private int page = 1;



}
