package cn.edu.iip.nju.web;

import cn.edu.iip.nju.common.PageHelper;
import cn.edu.iip.nju.model.InjureCase;
import cn.edu.iip.nju.service.CompanyNegativeListService;
import cn.edu.iip.nju.service.HospitalDataService;
import cn.edu.iip.nju.service.InjureCaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by xu on 2017/10/24.
 */
@Controller
public class InjureCaseController {

    private final InjureCaseService injureCaseService;
    private final CompanyNegativeListService companyNegativeListService;
    private final HospitalDataService hospitalDataService;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private final String baseInjurecaseSql = "SELECT * FROM injure_case ";

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
        } else {
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
            realSQL = baseInjurecaseSql + "where " + sql.toString().trim();
        } else {
            realSQL = baseInjurecaseSql;
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
        PageHelper<InjureCase> injureCases = injureCaseService.getByCondition(realSQL + " limit " + (page - 1) * 30 + "," + 30 + ";", page);
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

    @GetMapping("/comChart")
    public String comChart(Model model) {
        ArrayList<String> provs = Lists.newArrayList("北京市", "天津市", "河北省"
                , "山西省", "内蒙古自治区", "辽宁省", "吉林省", "黑龙江省"
                , "江苏省", "浙江省", "安徽省", "福建省", "江西省", "山东省"
                , "河南省", "湖北省", "湖南省", "广东省", "广西壮族自治区"
                , "海南省", "重庆市", "四川省", "贵州省", "云南省", "陕西省"
                , "甘肃省", "青海省", "宁夏回族自治区");

        TreeMap<String, Long> treeMap = Maps.newTreeMap();
        for (String prov : provs) {
            Long count = companyNegativeListService.countByProvinceName(prov);
            treeMap.put(prov, count);
        }
        model.addAttribute("provs", treeMap);
        Collection<Long> values = treeMap.values();
        long max = findMax(values);
        model.addAttribute("max", max);
        return "chart/company";

    }

    private long findMax(Collection<Long> nums) {
        return Collections.max(nums);
    }

    @GetMapping("/comProv")
    public String comPov(Model model,
                         @RequestParam(name = "pro") String pro) {
        long good = companyNegativeListService.countAllByProvinceAndPassPercentBetween(pro, 0.8, 1.0);
        long pass = companyNegativeListService.countAllByProvinceAndPassPercentBetween(pro, 0.6, 0.8);
        long common = companyNegativeListService.countAllByProvinceAndPassPercentBetween(pro, 0.4, 0.6);
        long bad = companyNegativeListService.countAllByProvinceAndPassPercentBetween(pro, 0.0, 0.4);
        long[] percent = {good, pass, common, bad};
        model.addAttribute("pro", pro);
        model.addAttribute("percent", percent);
        return "chart/provCom";
    }


    @GetMapping("/hosChart")
    public String hosChart(Model model) {
        Set<String> allLocations = hospitalDataService.getLocations();
        Map<String, Long> map = Maps.newHashMap();
        for (String location : allLocations) {
            Long count = hospitalDataService.countByLocation(location);
            map.put(location, count);
        }
        model.addAttribute("map", map);
        List<Integer> list = Lists.newArrayList();
        for (int i = 1; i <= 12; i++) {
            list.add(hospitalDataService.countByMonth(i));
        }
        model.addAttribute("list", list);
        return "chart/hos";
    }



    @GetMapping("/injureChart")
    public String injureChart(Model model){
        ArrayList<String> provs = Lists.newArrayList("北京市", "天津市", "河北省"
                , "山西省", "内蒙古自治区", "辽宁省", "吉林省", "黑龙江省"
                , "江苏省", "浙江省", "安徽省", "福建省", "江西省", "山东省"
                , "河南省", "湖北省", "湖南省", "广东省", "广西壮族自治区"
                , "海南省", "重庆市", "四川省", "贵州省", "云南省", "陕西省"
                , "甘肃省", "青海省", "宁夏回族自治区","上海市","新疆维吾尔自治区");
        TreeMap<String, Long> treeMap = Maps.newTreeMap();
        for (String prov : provs) {
            long count = injureCaseService.countByProv("%" + prov + "%");
            treeMap.put(prov,count);
        }
        Collection<Long> values = treeMap.values();
        long max = findMax(values);
        model.addAttribute("max", max);
        model.addAttribute("treemap",treeMap);
        return "chart/injurecase";
    }


//    @ResponseBody
//    @GetMapping("/injureChart")
//    public Map<String, Integer> injureChart() {
//
//        int pageNum = injureCaseService.totalPages();
//        for (int i = 0; i < pageNum; i++) {
//            Page<InjureCase> page = injureCaseService.findAll(new PageRequest(i, 1000));
//            injureCaseService.countByProv(page);
//        }
//        Map<String, Integer> map = injureCaseService.getMap();
//        for (Map.Entry<String, Integer> stringIntegerEntry : map.entrySet()) {
//            System.out.println(stringIntegerEntry.getKey()+"----"+stringIntegerEntry.getValue());
//        }
//        return map;
//    }


}
