package cn.edu.iip.nju.web;

import cn.edu.iip.nju.model.InjureCase;
import cn.edu.iip.nju.common.PageHelper;
import cn.edu.iip.nju.service.InjureCaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xu on 2017/10/24.
 */
@Controller
public class InjureCaseController {
    @Autowired
    private InjureCaseService injureCaseService;
    private final String baseSql = "SELECT * FROM injure_case WHERE ";
    private final Logger logger = LoggerFactory.getLogger(InjureCaseController.class);

    @GetMapping("/injurecase")
    public String injureCase(Model model,
                             @RequestParam(name = "page", defaultValue = "1") Integer page,
                             @RequestParam(name = "name", required = false,defaultValue = "") String name,
                             @RequestParam(name = "type", required = false,defaultValue = "") String type,
                             @RequestParam(name = "area", required = false,defaultValue = "") String area,
                             @RequestParam(name = "datefrom", required = false,defaultValue = "") String datefrom,
                             @RequestParam(name = "dateto", required = false,defaultValue = "") String dateto,
                             @RequestParam(name = "degree", required = false,defaultValue = "") String degree) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder sql = new StringBuilder();
//        String sql = "SELECT * FROM injure_case WHERE product_name='product47' AND injure_area='area5'";
        if(!name.isEmpty()){
            sql.append("product_name='").append(name).append("' and ");
        }
        if(!type.isEmpty()){
            sql.append("injure_type='").append(type).append("' and ");
        }
        if(!area.isEmpty()){
            sql.append("injure_area='").append(area).append("' and ");
        }
        if(!degree.isEmpty()){
            sql.append("injure_degree='").append(degree).append("' and ");
        }


        //字段拼接完成
        if(datefrom.isEmpty()){
            datefrom = "2000-01-01";
        }
        if (dateto.isEmpty()){
            dateto = sdf.format(new Date());
        }
        if(!(sql.length()>0) && datefrom.isEmpty() && dateto.isEmpty()){
            model.addAttribute("injureCases", injureCaseService.getInjureCases(new PageRequest(page,15)));
        }else{
            String resultSql = baseSql + sql.toString();

            resultSql+=" injure_time BETWEEN '"+datefrom+"' AND '"+dateto+"';";
            PageHelper<InjureCase> injureCases = injureCaseService.getByCondition(resultSql,page);
            model.addAttribute("injureCases", injureCases);
        }
        return "injureCase";
    }
}
