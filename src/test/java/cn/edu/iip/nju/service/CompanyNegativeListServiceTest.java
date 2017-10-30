package cn.edu.iip.nju.service;

import cn.edu.iip.nju.ZhijianjuApplication;
import cn.edu.iip.nju.dao.AttachmentDataDao;
import cn.edu.iip.nju.dao.CompanyNegativeListDao;
import cn.edu.iip.nju.model.CompanyNegativeList;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

/**
 * Created by xu on 2017/10/26.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZhijianjuApplication.class)
public class CompanyNegativeListServiceTest {
    @Test
    public void countByProvinceName() throws Exception {
        ArrayList<String> provs = Lists.newArrayList("北京市", "天津市", "河北省"
                , "山西省", "内蒙古自治区", "辽宁省", "吉林省", "黑龙江省"
                , "江苏省", "浙江省", "安徽省", "福建省", "江西省", "山东省"
                , "河南省", "湖北省", "湖南省", "广东省", "广西壮族自治区"
                , "海南省", "重庆市", "四川省", "贵州省", "云南省", "陕西省"
                , "甘肃省", "青海省", "宁夏回族自治区");
        for (String prov : provs) {
            System.out.println(companyNegativeListService.countByProvinceName(prov));
        }

    }

    @Test
    public void setProvince() throws Exception {
        Page<CompanyNegativeList> page = companyNegativeListDao.findAll(new PageRequest(0, 1000));
        for (int i = 0; i < page.getTotalPages(); i++) {
            Page<CompanyNegativeList> all = companyNegativeListDao.findAll(new PageRequest(i, 1000));
            companyNegativeListService.setProvince(all);
        }

    }

    @Autowired
    private CompanyNegativeListService companyNegativeListService;
    @Autowired
    private CompanyNegativeListDao companyNegativeListDao;
    @Autowired
    private AttachmentDataDao attachmentDataDao;
    @Test
    public void tagProcess() throws Exception {
        companyNegativeListService.tagProcess();
    }
    @Test
    public void test1(){
        Integer integer1 = attachmentDataDao.countAllByFactoryNameAndResult("昆山市长江电线电缆厂", "合格");
        Integer integer2 = attachmentDataDao.countAllByFactoryNameAndResult("昆山市长江电线电缆厂", "不合格");
        System.out.println(integer1);
        System.out.println(integer2);
    }

}