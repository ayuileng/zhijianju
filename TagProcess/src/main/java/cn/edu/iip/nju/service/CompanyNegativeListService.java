package cn.edu.iip.nju.service;

import cn.edu.iip.nju.dao.CompanyNegativeListDao;
import cn.edu.iip.nju.model.CompanyNegativeList;
import cn.edu.iip.nju.util.CityProvince;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by xu on 2017/10/26.
 * 完成产品负面清单的创建，数据来源是附件中的信息
 */
@Service
public class CompanyNegativeListService {
    private static Logger logger = LoggerFactory.getLogger(CompanyNegativeListService.class);

    @Autowired
    private CompanyNegativeListDao companyNegativeListDao;
    @Autowired
    AttachmentDataService attachmentDataService;

    /**
     * 根据附件的数据生成企业负面清单
     * //企业名称
     * //案例数
     * //伤害程度
     * //召回次数
     * //抽查合格率
     * //企业所在省份
     */
    public void tagProcess() {
        Map<String, Integer> zhaohuiFactory = getZhaohuiFactory();
        Set<String> companyName = attachmentDataService.getAllCompanys();
        //每一个企业的遍历
        for (String s : companyName) {
            CompanyNegativeList companyNegativeList = new CompanyNegativeList();
            companyNegativeList.setCompanyName(s);//企业名称
            String province = "";
            province = CityProvince.chooseProvinceOfCompany(s);
            if (!Strings.isNullOrEmpty(province)) {
                companyNegativeList.setProvince(province);//所在省份
            } else {
                String provinceFromCompanyName = getProvinceFromCompanyName(s);
                companyNegativeList.setProvince(provinceFromCompanyName);
            }
            long numsOfFactory = attachmentDataService.numsOfFactory(s);
            companyNegativeList.setCaseNum((int) numsOfFactory);//案例数
            long pass = attachmentDataService.numsOfFactoryByResult(s, "合格");
            long notPass = attachmentDataService.numsOfFactoryByResult(s, "不合格");
            if (pass + notPass == 0) {
                companyNegativeList.setPassPercent(-1.0);
            } else {
                companyNegativeList.setPassPercent(pass / (pass + notPass + 0.0));//合格率
            }

            companyNegativeList.setPassCase((int) pass);
            companyNegativeList.setUnPassCase((int) notPass);
            if (zhaohuiFactory.get(companyNegativeList.getCompanyName()) != null && zhaohuiFactory.get(companyNegativeList.getCompanyName()) > 0) {
                companyNegativeList.setCallbackNum(zhaohuiFactory.get(companyNegativeList.getCompanyName()));
                zhaohuiFactory.remove(companyNegativeList.getCompanyName());
            }
            //companyNegativeList.setCallbackNum(0);
            companyNegativeListDao.save(companyNegativeList);
            logger.info("saving 企业负面信息");
        }
        for (Map.Entry<String, Integer> entry : zhaohuiFactory.entrySet()) {
            CompanyNegativeList companyNegativeList = new CompanyNegativeList();
            companyNegativeList.setProvince(getProvinceFromCompanyName(entry.getKey()));
            companyNegativeList.setCompanyName(entry.getKey());
            companyNegativeList.setCaseNum(entry.getValue());
            companyNegativeList.setCallbackNum(entry.getValue());
            companyNegativeListDao.save(companyNegativeList);
        }
        logger.info("企业负面清单 save done");

    }

    /**
     * 大概3000多个无法匹配出省份
     * 金正大生态工程集团股份有限公司
     *
     * @param companyName
     * @return
     */
    private String getProvinceFromCompanyName(String companyName) {
        if (companyName.contains("北京")) {
            return "北京";
        } else if (companyName.contains("上海")) {
            return "上海";
        } else if (companyName.contains("重庆")) {
            return "重庆";
        } else if (companyName.contains("天津")) {
            return "天津";
        }
        String url = "http://www.xizhi.com/search?wd=" + companyName;
        logger.info("from net");
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .timeout(0)
                    .get();
            Element ul = doc.select("ul.result-list").first();
            if (ul == null) {
                return "";
            }
            Elements lis = ul.select("li");
            if (lis == null || lis.size() <= 0) {
                return "";
            }
            for (Element li : lis) {
                Element h3 = li.select("h3").first();
                if (Strings.isNullOrEmpty(h3.text()) && h3.text().equals(companyName)) {
                    Element p = li.select("i.addres-icon").first().parent();
                    String province = CityProvince.chooseProvinceOfCompany(p.text());
                    return province;
                }
            }
        } catch (IOException e) {
            logger.error("网络超时");
        }
        return "";
    }

    @Deprecated
    public void fixProvinceMissing() {
        final int pageSize = 100;
        Page<CompanyNegativeList> pageTmp = companyNegativeListDao.findAllByProvinceEquals("", new PageRequest(0, pageSize));
        int totalPages = pageTmp.getTotalPages();
        logger.info(totalPages + "");
        for (int i = totalPages - 1; i >= totalPages - 30; i--) {
            Page<CompanyNegativeList> page = companyNegativeListDao.findAllByProvinceEquals("", new PageRequest(0, pageSize));
            List<CompanyNegativeList> content = page.getContent();
            for (CompanyNegativeList companyNegativeList : content) {
                logger.info(companyNegativeList.getCompanyName());
                String province = CityProvince.chooseProvinceOfCompany(companyNegativeList.getCompanyName().trim());
                logger.info(province);
                if (Strings.isNullOrEmpty(province)) {
                    province = getProvinceFromCompanyName(companyNegativeList.getCompanyName().trim());
                    logger.info("->" + province);
                }
                companyNegativeList.setProvince(province);

            }
            companyNegativeListDao.save(content);
        }
        logger.info("fix done");
    }

    /**
     * http://www.dpac.gov.cn/xfpzh/xfpgnzh/
     * http://www.dpac.gov.cn/xfpzh/xfpzhgg/
     */
    public void fixCallBackNumMissing() {
        Map<String, Integer> zhaohuiFactory = getZhaohuiFactory();
        final int pageSize = 100;
        Page<CompanyNegativeList> pageTmp = companyNegativeListDao.findAllByProvinceIsNull(new PageRequest(0, pageSize));
        int totalPages = pageTmp.getTotalPages();
        for (int i = 0; i < totalPages; i++) {
            Page<CompanyNegativeList> page = companyNegativeListDao.findAllByProvinceIsNull(new PageRequest(i, pageSize));
            List<CompanyNegativeList> content = page.getContent();
            for (CompanyNegativeList companyNegativeList : content) {
                if (zhaohuiFactory.get(companyNegativeList.getCompanyName()) != null && zhaohuiFactory.get(companyNegativeList.getCompanyName()) > 0) {
                    companyNegativeList.setCallbackNum(zhaohuiFactory.get(companyNegativeList.getCompanyName()));
                    zhaohuiFactory.remove(companyNegativeList.getCompanyName());
                }

            }
            companyNegativeListDao.save(content);
        }
        for (Map.Entry<String, Integer> entry : zhaohuiFactory.entrySet()) {
            CompanyNegativeList companyNegativeList = new CompanyNegativeList();
            companyNegativeList.setPassCase(null);
            companyNegativeList.setUnPassCase(null);
            companyNegativeList.setProvince(getProvinceFromCompanyName(entry.getKey()));
            companyNegativeList.setCompanyName(entry.getKey());
            companyNegativeList.setCaseNum(entry.getValue());
            companyNegativeList.setInjureDegree(null);
            companyNegativeList.setCallbackNum(entry.getValue());
            companyNegativeList.setPassPercent(null);
            companyNegativeListDao.save(companyNegativeList);
        }
        logger.info("fix done");
    }

    /**
     * 页数写死在编码里了，偷懒的做法
     * 已经保存了
     *
     * @throws IOException
     */
    public void getCompanyZhaohui() throws IOException {
        List<String> urls = Lists.newArrayList();
        urls.add("http://www.dpac.gov.cn/xfpzh/xfpgnzh/");
        urls.add("http://www.dpac.gov.cn/xfpzh/xfpzhgg/");
        for (int i = 1; i < 23; i++) {
            urls.add("http://www.dpac.gov.cn/xfpzh/xfpzhgg/index_" + i + ".html");
        }
        for (int i = 1; i < 29; i++) {
            urls.add("http://www.dpac.gov.cn/xfpzh/xfpgnzh/index_" + i + ".html");
        }

        for (String url : urls) {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .timeout(0)
                    .get();
            Element ul = doc.select("div.boxl_ul").first();
            Elements lis = ul.select("li");
            for (Element li : lis) {
                String title = li.select("a[href]").first().text();
                if (!Strings.isNullOrEmpty(title)) {
                    if (title.startsWith("【")) {
                        title = title.substring(title.indexOf("】") + 1);
                    }
                    if (title.contains("有限公司")) {
                        title = title.substring(0, title.indexOf("有限公司")) + "有限公司";
                    }
                    if (title.contains("召回")) {
                        title = title.substring(0, title.indexOf("召回"));
                    }
                    System.out.println(title + "--" + li.select("a[href]").first().attr("abs:href"));
                }
            }
        }

    }

    private Map<String, Integer> getZhaohuiFactory() {
        Map<String, Integer> map = Maps.newHashMap();
        Resource resource = new ClassPathResource("keywords/zhaohui.txt");
        try {
            File file = resource.getFile();
            List<String> strings = Files.readLines(file, Charset.forName("utf-8"));
            for (String string : strings) {
                if (!map.containsKey(string)) {
                    map.put(string, 1);
                } else {
                    map.put(string, map.get(string) + 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 解决名称错误，比如带前缀标称的
     */
    @Transactional
    public void fixNameError() {
        Page<CompanyNegativeList> page = companyNegativeListDao.findAll(new PageRequest(0, 100));
        while (page.hasNext()) {
            List<CompanyNegativeList> content = page.getContent();
            List<CompanyNegativeList> errorList = content.stream().filter(c ->
                    c.getCompanyName().contains("标称")
            ).collect(Collectors.toList());
            for (CompanyNegativeList companyNegativeList : errorList) {
                String name = companyNegativeList.getCompanyName();
                if(name.endsWith("（标称）")){
                    name = name.substring(0,name.indexOf("（标称）"));
                }else if(name.startsWith("标称：")){
                    name = name.substring(name.indexOf("标称：")+"标称：".length());
                }else if(name.startsWith("标称:")){
                    name = name.substring(name.indexOf("标称:")+"标称:".length());
                }
                //System.out.println(name);
                companyNegativeList.setCompanyName(name);
            }
            companyNegativeListDao.save(errorList);
            page = companyNegativeListDao.findAll(page.nextPageable());
        }
        logger.info("process success");
    }


}
