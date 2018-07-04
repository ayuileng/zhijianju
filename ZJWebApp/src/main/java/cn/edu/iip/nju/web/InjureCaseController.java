package cn.edu.iip.nju.web;

import cn.edu.iip.nju.model.*;
import cn.edu.iip.nju.model.vo.CompanyForm;
import cn.edu.iip.nju.model.vo.HospitalForm;
import cn.edu.iip.nju.model.vo.InjureCaseForm;
import cn.edu.iip.nju.model.vo.ProductNegListForm;
import cn.edu.iip.nju.service.CompanyNegativeListService;
import cn.edu.iip.nju.service.HospitalDataService;
import cn.edu.iip.nju.service.InjureCaseService;
import cn.edu.iip.nju.service.NegProductService;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by xu on 2017/10/24.
 */
@Controller
public class InjureCaseController {

    private final InjureCaseService injureCaseService;
    private final CompanyNegativeListService companyNegativeListService;
    private final HospitalDataService hospitalDataService;
    private final NegProductService negProductService;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    @Autowired
    public InjureCaseController(InjureCaseService injureCaseService,
                                CompanyNegativeListService companyNegativeListService,
                                HospitalDataService hospitalDataService,
                                NegProductService negProductService) {
        this.injureCaseService = injureCaseService;
        this.companyNegativeListService = companyNegativeListService;
        this.hospitalDataService = hospitalDataService;
        this.negProductService = negProductService;
    }


    @GetMapping("/injurecase")
    public String injureCase(Model model, InjureCaseForm injureCaseForm) {
        Page<InjureCase> injureCases = injureCaseService.getByCondition(injureCaseForm);
        model.addAttribute("injureCases", injureCases);
        model.addAttribute("injureCaseForm", injureCaseForm);
        return "injureCase";
    }


    @GetMapping("/com")
    public String CompanyNegativeList(Model model, CompanyForm companyForm) {
        List<CompanyNegativeList> list = companyNegativeListService.getByCondition(companyForm);
//        List<CompanyDto> newList = Lists.newArrayList();
//        for (CompanyNegativeList companyNegativeList : list) {
//            CompanyDto companyDto = new CompanyDto();
//            BeanUtils.copyProperties(companyNegativeList,companyDto);
//            if(companyNegativeList.getCaseNum()>0){
//                companyDto.setUrls(getUrlList(companyNegativeList.getCompanyName()));
//            }
//        }
        model.addAttribute("pnl", list);
        model.addAttribute("companyForm", companyForm);
        return "comNegList";
    }

    private List<String> getUrlList(String companyName) {
        Map<String, List<String>> map = getCom2URLMap();
        return map.get(companyName);
    }

    private Map<String,List<String>> getCom2URLMap(){
        Map<String,List<String>> map = Maps.newHashMap();
        Resource resource = new ClassPathResource("keywords/com_url.txt");
        try {
            File file = resource.getFile();
            List<String> lines = Files.readLines(file, Charset.forName("utf-8"));
            for (String line : lines) {
                String[] split = line.split("--");
                if(map.containsKey(split[0])){
                    map.get(split[0]).add(split[1]);
                }else{
                    List<String> list  = Lists.newArrayList();
                    list.add(split[1]);
                    map.put(split[0],list);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    @GetMapping("/com/detail")
    public String getComDetail(@RequestParam("name") String name, Model model) {
        List<AttachmentData> company = companyNegativeListService.getCompanyDetail(name);
        for (AttachmentData attachmentData : company) {
            if (Strings.isNullOrEmpty(attachmentData.getBirthday())) {
                attachmentData.setBirthday("未知");
            }
            if (Strings.isNullOrEmpty(attachmentData.getShangbiao())) {
                attachmentData.setShangbiao("未知");
            }
            if (Strings.isNullOrEmpty(attachmentData.getErrorReason())) {
                attachmentData.setErrorReason("未知");
            }
            if (Strings.isNullOrEmpty(attachmentData.getChengjianjigou())) {
                attachmentData.setChengjianjigou("未知");
            }
        }
        model.addAttribute("companyDetail", company);
        System.out.println(company);
        return "comDetail";
    }

    @GetMapping("/com/callbackDetail")
    public String getComCallbackDetail(@RequestParam String companyName,Model model){
        Map<String, List<String>> urlMap = getCom2URLMap();
        List<String> urlList = urlMap.get(companyName);
        model.addAttribute("urlList",urlList);
        model.addAttribute("conpanyName",companyName);
        return "comCallbackDetail";
    }

    //根据时间、产品类别、伤害程度、与产品是否相关 4字段组合查询
    @GetMapping("/hospital")
    public String hospotal(Model model, HospitalForm hospitalDataForm) {
        Page<HospitalData> hos = hospitalDataService.getByCondition(hospitalDataForm);
        model.addAttribute("hos", hos);
        model.addAttribute("hospitalDataForm", hospitalDataForm);
        return "hospital";
    }

    @GetMapping("/productList")
    public String productList(Model model, ProductNegListForm form){
        System.out.println(form);
        List<NegProduct> list = negProductService.getByCondition(form);
        model.addAttribute("proList",list);
        model.addAttribute("form",form);
        return "negProList";
    }

    @GetMapping("/productList/productDetail")
    public String proDetail(Model model,ProductNegListForm form,String realName){
        List<NegProduct> list = negProductService.getByCondition(form);
        for (NegProduct negProduct : list) {
            if(realName.equals(negProduct.getProductName())){
                model.addAttribute("product",negProduct);
            }
        }
        return "proDetail";

    }


}
