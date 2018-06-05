package cn.edu.iip.nju.web;

import cn.edu.iip.nju.service.CompanyNegativeListService;
import cn.edu.iip.nju.service.HospitalDataService;
import cn.edu.iip.nju.service.InjureCaseService;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@RequestMapping("/chart")
public class ChartController {
    @Autowired
    InjureCaseService injureCaseService;
    @Autowired
    HospitalDataService hospitalDataService;
    @Autowired
    CompanyNegativeListService companyNegativeListService;

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
            treeMapd1.put(prov, count1);
            long count2 = injureCaseService.countByProvAndInjureDegree(tmp, "2");
            treeMapd2.put(prov, count2);
            long count3 = injureCaseService.countByProvAndInjureDegree(tmp, "3");
            treeMapd3.put(prov, count3);
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

    @GetMapping("/hosChart")
    public String hosChart(Model model) {
        Set<String> allLocations = hospitalDataService.getLocations();
        Map<String, Long> map = Maps.newHashMap();
        for (String location : allLocations) {
            if (!Strings.isNullOrEmpty(location)) {
                Long count = hospitalDataService.countByLocation(location);
                map.put(location, count);
            }
        }
        model.addAttribute("map", map);
        List<Integer> list = Lists.newArrayList();
        for (int i = 1; i <= 12; i++) {
            list.add(hospitalDataService.countByMonth(i));
        }
        model.addAttribute("list", list);
        String[] pcakey = {"服装", "家庭生活用品", "文体用品", "锐器", "玩具", "家用电器", "其他"};
        String[] pcavalue = {"79.35", "88.33", "85.19", "88.01", "77.49", "84.19", "91.21"};
        model.addAttribute("pcakey", pcakey);
        model.addAttribute("pcavalue", pcavalue);
        return "chart/hos";
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




    private long findMax(Collection<Long> nums) {
        return Collections.max(nums);
    }
}
