package cn.edu.iip.nju.web;

import cn.edu.iip.nju.common.PageHelper;
import cn.edu.iip.nju.model.HospitalData;
import cn.edu.iip.nju.model.InjureCase;
import cn.edu.iip.nju.model.form.HospitalForm;
import cn.edu.iip.nju.model.form.InjureCaseForm;
import cn.edu.iip.nju.service.CompanyNegativeListService;
import cn.edu.iip.nju.service.HospitalDataService;
import cn.edu.iip.nju.service.InjureCaseService;
import com.google.common.base.Strings;
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
    private final String hospitalCountSQL = "select count(1) from hospital_data";
    private final String hospitalSQL = "select * from hospital_data";

    @Autowired
    public InjureCaseController(InjureCaseService injureCaseService,
                                CompanyNegativeListService companyNegativeListService,
                                HospitalDataService hospitalDataService) {
        this.injureCaseService = injureCaseService;
        this.companyNegativeListService = companyNegativeListService;
        this.hospitalDataService = hospitalDataService;
    }



    @GetMapping("/injurecase")
    public String injureCase(Model model,InjureCaseForm injureCaseForm) {
        //sql模板："select * from xxx where d1=1 and d2=2 ... and time between t1 and t2 order by dx"
        StringBuilder sql = new StringBuilder();
        if (!Strings.isNullOrEmpty(injureCaseForm.getName())) {
            sql.append("product_name='").append(injureCaseForm.getName()).append("' and ");
        }else{
            sql.append("product_name!='").append("' and ");
        }
        if (!Strings.isNullOrEmpty(injureCaseForm.getType())) {
            sql.append("injure_type='").append(injureCaseForm.getType()).append("' and ");
        }else{
            sql.append("injure_type!='").append("' and ");
        }
        if (!Strings.isNullOrEmpty(injureCaseForm.getArea())) {
            sql.append("injure_area='").append(injureCaseForm.getArea()).append("' and ");
        }
        if (!Strings.isNullOrEmpty(injureCaseForm.getDegree())) {
            sql.append("injure_degree='").append(injureCaseForm.getDegree()).append("' and ");
        }
        if (!Strings.isNullOrEmpty(injureCaseForm.getDatefrom()) || !Strings.isNullOrEmpty(injureCaseForm.getDateto())) {
            if (Strings.isNullOrEmpty(injureCaseForm.getDatefrom())) {
                injureCaseForm.setDatefrom("2000-01-01");
            }
            if (Strings.isNullOrEmpty(injureCaseForm.getDateto())) {
                injureCaseForm.setDateto(sdf.format(new Date()));
            }
            sql.append("injure_time BETWEEN '").append(injureCaseForm.getDatefrom()).append("' and '").append(injureCaseForm.getDateto()).append("' and ");
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
        if ("time1".equals(injureCaseForm.getSort())) {

            realSQL += " ORDER BY injure_time ASC ";
        } else if("time2".equals(injureCaseForm.getSort())){
            realSQL += " ORDER BY injure_time DESC ";
        }else if("degree2".equals(injureCaseForm.getSort())){
            realSQL += " ORDER BY injure_degree ASC ";
        }else if("degree1".equals(injureCaseForm.getSort())){
            realSQL += " ORDER BY injure_degree DESC ";
        }else {
            realSQL += " ORDER BY injure_time ASC ";
        }
        PageHelper<InjureCase> injureCases = injureCaseService.getByCondition(realSQL + " limit " + (injureCaseForm.getPage() - 1) * 40 + "," + 40 + ";", injureCaseForm.getPage());
        model.addAttribute("injureCases", injureCases);
        model.addAttribute("injureCaseForm", injureCaseForm);

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

    //根据时间、产品类别、伤害程度、与产品是否相关 4字段组合查询
    @GetMapping("/hospital")
    public String hospotal(Model model,
                           HospitalForm hospitalDataForm) {
        String realSQL,realCountSQL;
        StringBuilder sql = new StringBuilder();
        if (!Strings.isNullOrEmpty(hospitalDataForm.getProductType())) {
            sql.append("product='").append(hospitalDataForm.getProductType()).append("' and ");
        }
        if(!Strings.isNullOrEmpty(hospitalDataForm.getInjureDegree())){
            sql.append("injure_degree='").append(hospitalDataForm.getInjureDegree()).append("' and ");
        }
        if(!Strings.isNullOrEmpty(hospitalDataForm.getIfIntention())){
            sql.append("how_get_injure='").append(hospitalDataForm.getIfIntention()).append("' and ");
        }
        if (!Strings.isNullOrEmpty(hospitalDataForm.getDatefrom()) || !Strings.isNullOrEmpty(hospitalDataForm.getDateto())) {
            if (Strings.isNullOrEmpty(hospitalDataForm.getDatefrom())) {
                hospitalDataForm.setDatefrom("2014-01-01");
            }
            if (Strings.isNullOrEmpty(hospitalDataForm.getDateto())) {
                hospitalDataForm.setDateto(sdf.format(new Date()));
            }
            sql.append("injure_date BETWEEN '").append(hospitalDataForm.getDatefrom()).append("' and '").append(hospitalDataForm.getDateto()).append("' and ");
        }
        if(sql.toString().trim().length()>0){
            realSQL = hospitalSQL+" where "+sql.toString().trim();
            realCountSQL = hospitalCountSQL+" where "+sql.toString().trim();
        }else{
            realSQL = hospitalSQL;
            realCountSQL = hospitalCountSQL;
        }
        if (realSQL.endsWith("and")) {
            realSQL = realSQL.substring(0, realSQL.length() - 3);
            realCountSQL = realCountSQL.substring(0, realCountSQL.length() - 3);
        }
        PageHelper<HospitalData> pageData = hospitalDataService.getListData(realSQL + " limit " + (hospitalDataForm.getPage() - 1) * 50 + "," + 50 + ";", hospitalDataForm.getPage(), realCountSQL);
        model.addAttribute("hos", pageData);
        model.addAttribute("hospitalDataForm", hospitalDataForm);
        model.addAttribute("count", pageData.getTotalRows());
        String params = "/hospital?injure_degree="+hospitalDataForm.getInjureDegree()
                +"&productType="+hospitalDataForm.getProductType()
                +"&ifIntention="+hospitalDataForm.getIfIntention()
                +"&datefrom="+hospitalDataForm.getDatefrom()
                +"&dateto="+hospitalDataForm.getDateto()
                +"&page=";
        model.addAttribute("params",params);
        System.out.println(hospitalDataForm);
        System.out.println(params);
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
    public String injureChart(Model model) {
        ArrayList<String> provs = Lists.newArrayList("北京市", "天津市", "河北省"
                , "山西省", "内蒙古自治区", "辽宁省", "吉林省", "黑龙江省"
                , "江苏省", "浙江省", "安徽省", "福建省", "江西省", "山东省"
                , "河南省", "湖北省", "湖南省", "广东省", "广西壮族自治区"
                , "海南省", "重庆市", "四川省", "贵州省", "云南省", "陕西省"
                , "甘肃省", "青海省", "宁夏回族自治区", "上海市", "新疆维吾尔自治区");
        TreeMap<String, Long> treeMap = Maps.newTreeMap();
        TreeMap<String, Long> treeMapd1 = Maps.newTreeMap();
        TreeMap<String, Long> treeMapd2 = Maps.newTreeMap();
        TreeMap<String, Long> treeMapd3 = Maps.newTreeMap();
        for (String prov : provs) {
            String tmp = "%" + prov + "%";
            long count = injureCaseService.countByProv(tmp);
            treeMap.put(prov, count);
            long count1 = injureCaseService.countByProvAndInjureDegree(tmp, "1");
            treeMapd1.put(prov,count1);
            long count2 = injureCaseService.countByProvAndInjureDegree(tmp, "2");
            treeMapd2.put(prov,count2);
            long count3 = injureCaseService.countByProvAndInjureDegree(tmp, "3");
            treeMapd3.put(prov,count3);
        }
        long max = findMax(treeMap.values());
        long maxd1 = findMax(treeMapd1.values());
        long maxd2 = findMax(treeMapd2.values());
        long maxd3 = findMax(treeMapd3.values());
        model.addAttribute("max", max);
        model.addAttribute("maxd1", maxd1);
        model.addAttribute("maxd2", maxd2);
        model.addAttribute("maxd3", maxd3);
        model.addAttribute("treemap", treeMap);
        model.addAttribute("treemapd1", treeMapd1);
        model.addAttribute("treemapd2", treeMapd2);
        model.addAttribute("treemapd3", treeMapd3);
        return "chart/injurecase";
    }



}
