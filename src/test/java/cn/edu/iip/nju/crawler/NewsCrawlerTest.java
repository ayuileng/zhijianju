package cn.edu.iip.nju.crawler;

import cn.edu.iip.nju.ZhijianjuApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

/**
 * Created by xu on 2017/11/29.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZhijianjuApplication.class)
@Rollback(value = false)
public class NewsCrawlerTest {
    @Autowired
    private NewsCrawler newsCrawler;
    @Test
    public void startNewsCrawler() throws Exception {

        Set<String> s = newsCrawler.baiduCrawler("围栏窒息");
        for (String s1 : s) {
            System.out.println(s1);
        }
    }

    @Test
    public void readKeyWords() throws Exception {
    }

}