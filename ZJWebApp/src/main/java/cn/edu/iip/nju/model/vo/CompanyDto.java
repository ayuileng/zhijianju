package cn.edu.iip.nju.model.vo;

import cn.edu.iip.nju.model.CompanyNegativeList;
import lombok.Data;

import java.util.List;

@Data
public class CompanyDto extends CompanyNegativeList {
    private List<String> urls;
}
