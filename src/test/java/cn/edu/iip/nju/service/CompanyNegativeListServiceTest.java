package cn.edu.iip.nju.service;

import cn.edu.iip.nju.ZhijianjuApplication;
import cn.edu.iip.nju.dao.AttachmentDataDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by xu on 2017/10/26.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZhijianjuApplication.class)
public class CompanyNegativeListServiceTest {
    @Autowired
    private CompanyNegativeListService companyNegativeListService;
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