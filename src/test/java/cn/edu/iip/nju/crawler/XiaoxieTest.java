package cn.edu.iip.nju.crawler;

import cn.edu.iip.nju.ZhijianjuApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Set;

/**
 * Created by xu on 2017/7/31.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ZhijianjuApplication.class)
public class XiaoxieTest {
    @Autowired
    private Xiaoxie xiaoxie;
    @Test
    public void getCCAurl() throws Exception {
        Set<String> ccAurl = xiaoxie.getCCAurl();
        for (String s : ccAurl) {
            System.out.println(s);
        }
    }

    @Test
    public void testPostTime() throws Exception {
        xiaoxie.process_cca_urls();
    }

    @Test
    public void test315() throws Exception {
        xiaoxie.getSanyaowuUrls();
    }

}