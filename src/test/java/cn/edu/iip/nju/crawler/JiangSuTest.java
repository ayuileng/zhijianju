package cn.edu.iip.nju.crawler;

import cn.edu.iip.nju.ZhijianjuApplication;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by xu on 2017/8/1.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZhijianjuApplication.class)
public class JiangSuTest {
    @Autowired
    private JiangSu jiangSu ;
    @Test
    public void start() throws Exception {
        jiangSu.start();
    }

    @Test
    public void getPageNum() throws Exception {
        String pageNum = jiangSu.getPageNum();
        System.out.println(pageNum);
    }

    @Test
    public void getAllPages() throws Exception {
        ArrayList<Document> allPages = jiangSu.getAllPages();
        System.out.println(allPages.size());
    }

    @Test
    public void getAllUrl() throws Exception {
        Set<String> allUrl = jiangSu.getAllUrl();
        allUrl.forEach(System.out::println);
    }

    @Test
    public void process_each_url() throws Exception {
        Map<String, Set<String>> stringSetMap = jiangSu.process_each_url();
        stringSetMap.forEach((s, strings) -> strings.forEach(System.out::println));
    }

}