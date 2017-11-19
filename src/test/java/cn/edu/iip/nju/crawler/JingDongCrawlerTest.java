package cn.edu.iip.nju.crawler;

import cn.edu.iip.nju.ZhijianjuApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by xu on 2017/11/18.
 */
@RunWith(SpringRunner.class)
@Rollback(value = false)
@SpringBootTest(classes = ZhijianjuApplication.class)
public class JingDongCrawlerTest {
    @Autowired
    private JingDongCrawler jingDongCrawler;
    @Test
    public void start() throws Exception {
        jingDongCrawler.startJingdongCrawler();

    }

}