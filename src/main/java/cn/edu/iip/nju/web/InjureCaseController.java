package cn.edu.iip.nju.web;

import cn.edu.iip.nju.common.PageHelper;
import cn.edu.iip.nju.model.InjureCase;
import cn.edu.iip.nju.service.CompanyNegativeListService;
import cn.edu.iip.nju.service.HospitalDataService;
import cn.edu.iip.nju.service.InjureCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    private final InjureCaseService injureCaseService;
    private final CompanyNegativeListService companyNegativeListService;
    private final HospitalDataService hospitalDataService;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private final String baseSql = "SELECT * FROM injure_case ";

    @Autowired
    public InjureCaseController(InjureCaseService injureCaseService,
                                CompanyNegativeListService companyNegativeListService,
                                HospitalDataService hospitalDataService) {
        this.injureCaseService = injureCaseService;
        this.companyNegativeListService = companyNegativeListService;
        this.hospitalDataService = hospitalDataService;
    }


    @GetMapping("/injurecase")
    public String injureCase(Model model,
                             @RequestParam(name = "page", defaultValue = "1") Integer page,
                             @RequestParam(name = "name", required = false, defaultValue = "") String name,
                             @RequestParam(name = "type", required = false, defaultValue = "") String type,
                             @RequestParam(name = "area", required = false, defaultValue = "") String area,
                             @RequestParam(name = "datefrom", required = false, defaultValue = "") String datefrom,
                             @RequestParam(name = "dateto", required = false, defaultValue = "") String dateto,
                             @RequestParam(name = "degree", required = false, defaultValue = "") String degree,
                             @RequestParam(name = "sort", required = false, defaultValue = "1") Integer sort) {
        StringBuilder sql = new StringBuilder();
//        String sql = "SELECT * FROM injure_case WHERE product_name='product47' AND injure_area='area5'";
        if (!name.isEmpty()) {
            sql.append("product_name='").append(name).append("' and ");
        }
        if (!type.isEmpty()) {
            sql.append("injure_type='").append(type).append("' and ");
        }else {
            sql.append("injure_type!='' and ");
        }
        if (!area.isEmpty()) {
            sql.append("injure_area='").append(area).append("' and ");
        }
        if (!degree.isEmpty()) {
            sql.append("injure_degree='").append(degree).append("' and ");
        }
        if (!datefrom.isEmpty() || !dateto.isEmpty()) {
            if (datefrom.isEmpty()) {
                datefrom = "2000-01-01";
            }
            if (dateto.isEmpty()) {
                dateto = sdf.format(new Date());
            }
            sql.append("injure_time BETWEEN '").append(datefrom).append("' and '").append(dateto).append("' and ");
        }
        String realSQL;
        if (sql.toString().trim().length() > 0) {
            realSQL = baseSql + "where " + sql.toString().trim();
        }else {
            realSQL = baseSql;
        }
        if (realSQL.endsWith("and")) {
            realSQL = realSQL.substring(0, realSQL.length() - 3);
        }
        if (sort.equals(1)) {
            //1表示按伤害指数排序,2表示按照伤害发生时间排序
            realSQL += " ORDER BY injure_index DESC ";
        } else {
            realSQL += " ORDER BY injure_time DESC ";
        }
        PageHelper<InjureCase> injureCases = injureCaseService.getByCondition(realSQL + " limit "+(page-1)*30+","+30+";", page);
        model.addAttribute("injureCases", injureCases);

        return "injureCase";
    }


    @GetMapping("/com")
    public String productNegativeList(Model model,
                                      @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
                                      @RequestParam(name = "sort", defaultValue = "1", required = false) String sort) {
        Sort s;
        if (sort.equals("1")) {
            s = new Sort(Sort.Direction.DESC, "passPercent");
        } else if (sort.equals("2")) {
            s = new Sort(Sort.Direction.ASC, "passPercent");
        } else if (sort.equals("3")) {
            s = new Sort(Sort.Direction.DESC, "caseNum");
        } else {
            s = new Sort(Sort.Direction.ASC, "caseNum");
        }

        model.addAttribute("pnl", companyNegativeListService.getAll(new PageRequest(page, 40, s)));
        return "comNegList";
    }

    @GetMapping("/hospital")
    public String hospotal(Model model,
                           @RequestParam(name = "page", defaultValue = "0", required = false) Integer page) {
        model.addAttribute("hos", hospitalDataService.getByPage(new PageRequest(page, 30)));
        return "hospital";
    }

//    @GetMapping("/comChart")
//    public String comChart(Model model){
//
//    }
}
