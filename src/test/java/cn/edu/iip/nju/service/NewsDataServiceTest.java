package cn.edu.iip.nju.service;

import cn.edu.iip.nju.ZhijianjuApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by xu on 2017/12/6.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZhijianjuApplication.class)
public class NewsDataServiceTest {
    @Autowired
    private NewsDataService newsDataService;
    @Test
    public void pageNum() throws Exception {
//        System.out.println(newsDataService.pageNum());
    }

    @Test
    public void saveInjureCase() throws Exception {
        newsDataService.saveInjureCase();
    }

}