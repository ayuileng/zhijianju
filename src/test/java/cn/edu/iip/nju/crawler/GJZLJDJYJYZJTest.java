package cn.edu.iip.nju.crawler;

import cn.edu.iip.nju.ZhijianjuApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Set;

/**
 * Created by xu on 2017/8/1.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZhijianjuApplication.class)
public class GJZLJDJYJYZJTest {
    @Test
    public void getUrl() throws Exception {
        Set<String> url = gjzljdjyjyzj.getUrl();

    }

    @Test
    public void getAllPages() throws Exception {
        Set<String> allPages = gjzljdjyjyzj.getAllPages();
        allPages.forEach(System.out::println);
    }

    @Test
    public void process_each_url() throws Exception {
        Map<String, Set<String>> stringSetMap = gjzljdjyjyzj.process_each_url();
    }

    @Autowired
    private GJZLJDJYJYZJ gjzljdjyjyzj;
    @Test
    public void getPageUrls() throws Exception {
        Set<String> pageUrls = gjzljdjyjyzj.getPageUrls();
        pageUrls.forEach(System.out::println);
    }

    @Test
    public void start() throws Exception {
        gjzljdjyjyzj.start();
    }

}